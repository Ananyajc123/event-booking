package com.eventbooking.service;

import com.eventbooking.dto.*;
import com.eventbooking.exception.EventNotFoundException;
import com.eventbooking.model.*;
import com.eventbooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired private EventRepository eventRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private VenueRepository venueRepository;

    public List<EventResponse> getAllEvents() {
        return eventRepository.findByIsActiveTrueOrderByEventDateAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByCategory(String category) {
        return eventRepository.findByCategoryAndIsActiveTrueOrderByEventDateAsc(category)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword, LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + id));
        return toResponse(event);
    }

    public SeatMapResponse getSeatMap(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        List<Seat> seats = seatRepository.findByEventIdOrderBySeatNumber(eventId);
        Long availableCount = seatRepository.countAvailableSeats(eventId);

        List<SeatMapResponse.SeatDTO> seatDTOs = seats.stream()
                .map(s -> new SeatMapResponse.SeatDTO(s.getId(), s.getSeatNumber(), s.getRow(), s.getSeatIndex(), s.getStatus().name()))
                .collect(Collectors.toList());

        SeatMapResponse response = new SeatMapResponse();
        response.setEventId(eventId);
        response.setEventName(event.getName());
        response.setTotalSeats(seats.size());
        response.setAvailableSeats(availableCount.intValue());
        response.setSeats(seatDTOs);
        return response;
    }

    @Transactional
    public EventResponse createEvent(Event event) {
        Venue venue = venueRepository.findById(event.getVenue().getId())
                .orElseThrow(() -> new EventNotFoundException("Venue not found"));
        event.setVenue(venue);
        Event saved = eventRepository.save(event);

        List<Seat> seats = new ArrayList<>();
        String[] rowLabels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        for (int r = 0; r < venue.getRows(); r++) {
            for (int s = 1; s <= venue.getSeatsPerRow(); s++) {
                Seat seat = new Seat();
                seat.setSeatNumber(rowLabels[r] + s);
                seat.setRow(rowLabels[r]);
                seat.setSeatIndex(s);
                seat.setEvent(saved);
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        return toResponse(saved);
    }

    private EventResponse toResponse(Event e) {
        EventResponse r = new EventResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setDescription(e.getDescription());
        r.setCategory(e.getCategory());
        r.setEventDate(e.getEventDate());
        r.setTicketPrice(e.getTicketPrice());
        r.setImageUrl(e.getImageUrl());
        r.setVenueName(e.getVenue() != null ? e.getVenue().getName() : "");
        r.setVenueLocation(e.getVenue() != null ? e.getVenue().getLocation() : "");
        r.setAvailableSeats(seatRepository.countAvailableSeats(e.getId()).intValue());
        return r;
    }
}
