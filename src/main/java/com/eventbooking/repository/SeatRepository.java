package com.eventbooking.repository;

import com.eventbooking.model.Seat;
import com.eventbooking.model.Seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventIdOrderBySeatNumber(Long eventId);

    List<Seat> findByEventIdAndStatus(Long eventId, SeatStatus status);

    // PESSIMISTIC WRITE lock — only one transaction can hold this at a time
    // This is the key to solving the concurrent booking problem
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    // Count available seats for an event
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.event.id = :eventId AND s.status = 'AVAILABLE'")
    Long countAvailableSeats(@Param("eventId") Long eventId);

    @Query("SELECT s FROM Seat s WHERE s.event.id = :eventId AND s.id IN :seatIds")
    List<Seat> findByEventIdAndIdIn(@Param("eventId") Long eventId,
                                     @Param("seatIds") List<Long> seatIds);
}
