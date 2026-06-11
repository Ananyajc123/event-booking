package com.eventbooking.service;

import com.eventbooking.dto.*;
import com.eventbooking.exception.*;
import com.eventbooking.model.*;
import com.eventbooking.model.Seat.SeatStatus;
import com.eventbooking.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired private BookingRepository bookingRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Value("${app.seat.lock.ttl-seconds:600}") private long seatLockTtl;

    private static final String SEAT_LOCK_PREFIX = "seat:lock:";
    private static final String BOOKING_LOCK_PREFIX = "booking:lock:event:";

    @Transactional
    public BookingInitResponse initBooking(BookingInitRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getIsActive() || event.getEventDate().isBefore(LocalDateTime.now()))
            throw new EventNotAvailableException("Event is not available for booking");

        String eventLockKey = BOOKING_LOCK_PREFIX + request.getEventId();
        String lockValue = UUID.randomUUID().toString();
        boolean acquired = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(eventLockKey, lockValue, 30, TimeUnit.SECONDS));
        if (!acquired) throw new SeatNotAvailableException("System is busy. Please try again.");

        try {
            List<Seat> seats = new ArrayList<>();
            for (Long seatId : request.getSeatIds()) {
                Seat seat = seatRepository.findByIdWithLock(seatId)
                        .orElseThrow(() -> new SeatNotFoundException("Seat not found: " + seatId));
                if (seat.getStatus() != SeatStatus.AVAILABLE)
                    throw new SeatNotAvailableException("Seat " + seat.getSeatNumber() + " is no longer available");
                if (!seat.getEvent().getId().equals(request.getEventId()))
                    throw new InvalidBookingException("Seat does not belong to this event");
                seat.setStatus(SeatStatus.LOCKED);
                seats.add(seat);
            }
            seatRepository.saveAll(seats);

            for (Seat seat : seats)
                redisTemplate.opsForValue().set(SEAT_LOCK_PREFIX + seat.getId(), userEmail, seatLockTtl, TimeUnit.SECONDS);

            BigDecimal total = event.getTicketPrice().multiply(BigDecimal.valueOf(seats.size()));

            Booking booking = new Booking();
            booking.setBookingReference(generateBookingReference());
            booking.setUser(user);
            booking.setEvent(event);
            booking.setSeats(seats);
            booking.setTotalAmount(total);
            booking.setStatus(Booking.BookingStatus.PENDING);
            booking.setExpiresAt(LocalDateTime.now().plusSeconds(seatLockTtl));
            bookingRepository.save(booking);

            log.info("Booking initiated: {} for {} seats on event {}", booking.getBookingReference(), seats.size(), event.getName());

            BookingInitResponse response = new BookingInitResponse();
            response.setBookingReference(booking.getBookingReference());
            response.setTotalAmount(total);
            response.setExpiresAt(booking.getExpiresAt());
            response.setSeats(seats.stream().map(Seat::getSeatNumber).collect(Collectors.toList()));
            return response;

        } finally {
            String cur = redisTemplate.opsForValue().get(eventLockKey);
            if (lockValue.equals(cur)) redisTemplate.delete(eventLockKey);
        }
    }

    @Transactional
    public BookingConfirmResponse confirmBooking(String bookingReference, String userEmail) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (!booking.getUser().getEmail().equals(userEmail))
            throw new UnauthorizedBookingException("Not your booking");
        if (booking.getStatus() != Booking.BookingStatus.PENDING)
            throw new InvalidBookingException("Booking is not in PENDING state");
        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            releaseSeats(booking);
            booking.setStatus(Booking.BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new BookingExpiredException("Booking has expired. Please try again.");
        }
        for (Seat seat : booking.getSeats()) seat.setStatus(SeatStatus.BOOKED);
        seatRepository.saveAll(booking.getSeats());
        for (Seat seat : booking.getSeats()) redisTemplate.delete(SEAT_LOCK_PREFIX + seat.getId());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        log.info("Booking confirmed: {}", bookingReference);

        BookingConfirmResponse r = new BookingConfirmResponse();
        r.setBookingReference(bookingReference);
        r.setStatus("CONFIRMED");
        r.setTotalAmount(booking.getTotalAmount());
        r.setEventName(booking.getEvent().getName());
        r.setEventDate(booking.getEvent().getEventDate());
        r.setSeats(booking.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.toList()));
        return r;
    }

    @Transactional
    public void cancelBooking(String bookingReference, String userEmail) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        if (!booking.getUser().getEmail().equals(userEmail))
            throw new UnauthorizedBookingException("Not your booking");
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED)
            throw new InvalidBookingException("Booking already cancelled");
        releaseSeats(booking);
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled: {}", bookingReference);
    }

    public List<BookingConfirmResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return bookingRepository.findByUserOrderByBookedAtDesc(user).stream().map(b -> {
            BookingConfirmResponse r = new BookingConfirmResponse();
            r.setBookingReference(b.getBookingReference());
            r.setStatus(b.getStatus().name());
            r.setTotalAmount(b.getTotalAmount());
            r.setEventName(b.getEvent().getName());
            r.setEventDate(b.getEvent().getEventDate());
            r.setSeats(b.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.toList()));
            return r;
        }).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void releaseExpiredBookings() {
        List<Booking> expired = bookingRepository.findExpiredBookings(LocalDateTime.now());
        for (Booking booking : expired) {
            releaseSeats(booking);
            booking.setStatus(Booking.BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            log.info("Released expired booking: {}", booking.getBookingReference());
        }
        if (!expired.isEmpty()) log.info("Released {} expired bookings", expired.size());
    }

    private void releaseSeats(Booking booking) {
        for (Seat seat : booking.getSeats()) {
            if (seat.getStatus() != SeatStatus.BOOKED) {
                seat.setStatus(SeatStatus.AVAILABLE);
                redisTemplate.delete(SEAT_LOCK_PREFIX + seat.getId());
            }
        }
        seatRepository.saveAll(booking.getSeats());
    }

    private String generateBookingReference() {
        return "BK-" + LocalDateTime.now().getYear() + "-" + String.format("%06d", new Random().nextInt(999999));
    }
}
