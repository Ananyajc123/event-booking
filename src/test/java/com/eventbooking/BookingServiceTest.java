package com.eventbooking;

import com.eventbooking.dto.BookingInitRequest;
import com.eventbooking.exception.SeatNotAvailableException;
import com.eventbooking.exception.EventNotAvailableException;
import com.eventbooking.model.*;
import com.eventbooking.repository.*;
import com.eventbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private BookingService bookingService;

    private User testUser;
    private Event testEvent;
    private Seat testSeat;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "seatLockTtl", 600L);

        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setName("Test");

        Venue venue = new Venue();
        venue.setName("Test Venue");

        testEvent = new Event();
        testEvent.setName("Test Event");
        testEvent.setTicketPrice(new BigDecimal("500"));
        testEvent.setEventDate(LocalDateTime.now().plusDays(10));
        testEvent.setIsActive(true);
        testEvent.setVenue(venue);
        ReflectionTestUtils.setField(testEvent, "id", 1L);

        testSeat = new Seat();
        testSeat.setSeatNumber("A1");
        testSeat.setRow("A");
        testSeat.setSeatIndex(1);
        testSeat.setStatus(Seat.SeatStatus.AVAILABLE);
        testSeat.setEvent(testEvent);
        ReflectionTestUtils.setField(testSeat, "id", 1L);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldInitBooking_whenSeatAvailable() {
        BookingInitRequest req = new BookingInitRequest(1L, List.of(1L));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);
        when(seatRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testSeat));
        when(seatRepository.saveAll(any())).thenReturn(List.of(testSeat));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = bookingService.initBooking(req, "test@test.com");

        assertNotNull(response.getBookingReference());
        assertEquals(new BigDecimal("500"), response.getTotalAmount());
    }

    @Test
    void shouldThrow_whenRedisLockNotAcquired() {
        BookingInitRequest req = new BookingInitRequest(1L, List.of(1L));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(false);

        assertThrows(SeatNotAvailableException.class,
                () -> bookingService.initBooking(req, "test@test.com"));
    }

    @Test
    void shouldThrow_whenSeatAlreadyLocked() {
        testSeat.setStatus(Seat.SeatStatus.LOCKED);
        BookingInitRequest req = new BookingInitRequest(1L, List.of(1L));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);
        when(seatRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testSeat));

        assertThrows(SeatNotAvailableException.class,
                () -> bookingService.initBooking(req, "test@test.com"));
    }

    @Test
    void shouldThrow_whenEventExpired() {
        testEvent.setEventDate(LocalDateTime.now().minusDays(1));
        BookingInitRequest req = new BookingInitRequest(1L, List.of(1L));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(EventNotAvailableException.class,
                () -> bookingService.initBooking(req, "test@test.com"));
    }

    @Test
    void shouldCalculateCorrectTotal_forMultipleSeats() {
        Seat seat2 = new Seat();
        seat2.setSeatNumber("A2");
        seat2.setRow("A");
        seat2.setSeatIndex(2);
        seat2.setStatus(Seat.SeatStatus.AVAILABLE);
        seat2.setEvent(testEvent);
        ReflectionTestUtils.setField(seat2, "id", 2L);

        BookingInitRequest req = new BookingInitRequest(1L, List.of(1L, 2L));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true);
        when(seatRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testSeat));
        when(seatRepository.findByIdWithLock(2L)).thenReturn(Optional.of(seat2));
        when(seatRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = bookingService.initBooking(req, "test@test.com");
        assertEquals(new BigDecimal("1000"), response.getTotalAmount());
    }
}
