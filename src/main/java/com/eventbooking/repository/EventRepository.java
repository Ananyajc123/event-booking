package com.eventbooking.repository;

import com.eventbooking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByIsActiveTrueOrderByEventDateAsc();

    List<Event> findByCategoryAndIsActiveTrueOrderByEventDateAsc(String category);

    @Query("""
        SELECT e FROM Event e
        WHERE e.isActive = true
        AND e.eventDate > :now
        AND (LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY e.eventDate ASC
    """)
    List<Event> searchEvents(@Param("keyword") String keyword,
                              @Param("now") LocalDateTime now);

    @Query("""
        SELECT e FROM Event e
        WHERE e.isActive = true
        AND e.eventDate BETWEEN :start AND :end
        ORDER BY e.eventDate ASC
    """)
    List<Event> findByDateRange(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}
