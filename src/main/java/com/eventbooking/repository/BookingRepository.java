package com.eventbooking.repository;

import com.eventbooking.model.Booking;
import com.eventbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByBookedAtDesc(User user);

    Optional<Booking> findByBookingReference(String bookingReference);

    // Find expired pending bookings for cleanup scheduler
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.expiresAt < :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.event.id = :eventId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookings(@Param("eventId") Long eventId);
}
