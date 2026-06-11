package com.eventbooking.controller;
import com.eventbooking.dto.*;
import com.eventbooking.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/auth")
class AuthController {
    @Autowired private AuthService authService;
    @PostMapping("/register") public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) { return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req)); }
    @PostMapping("/login") public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) { return ResponseEntity.ok(authService.login(req)); }
}

@RestController @RequestMapping("/api/events")
class EventController {
    @Autowired private EventService eventService;
    @GetMapping public ResponseEntity<List<EventResponse>> getAll(@RequestParam(required=false) String category, @RequestParam(required=false) String search) {
        if (search != null) return ResponseEntity.ok(eventService.searchEvents(search));
        if (category != null) return ResponseEntity.ok(eventService.getEventsByCategory(category));
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    @GetMapping("/{id}") public ResponseEntity<EventResponse> getById(@PathVariable Long id) { return ResponseEntity.ok(eventService.getEventById(id)); }
    @GetMapping("/{id}/seats") public ResponseEntity<SeatMapResponse> getSeatMap(@PathVariable Long id) { return ResponseEntity.ok(eventService.getSeatMap(id)); }
}

@RestController @RequestMapping("/api/bookings")
class BookingController {
    @Autowired private BookingService bookingService;
    @PostMapping("/init") public ResponseEntity<BookingInitResponse> initBooking(@Valid @RequestBody BookingInitRequest req, @AuthenticationPrincipal UserDetails user) { return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.initBooking(req, user.getUsername())); }
    @PostMapping("/{ref}/confirm") public ResponseEntity<BookingConfirmResponse> confirm(@PathVariable String ref, @AuthenticationPrincipal UserDetails user) { return ResponseEntity.ok(bookingService.confirmBooking(ref, user.getUsername())); }
    @DeleteMapping("/{ref}") public ResponseEntity<Void> cancel(@PathVariable String ref, @AuthenticationPrincipal UserDetails user) { bookingService.cancelBooking(ref, user.getUsername()); return ResponseEntity.noContent().build(); }
    @GetMapping("/my") public ResponseEntity<List<BookingConfirmResponse>> myBookings(@AuthenticationPrincipal UserDetails user) { return ResponseEntity.ok(bookingService.getUserBookings(user.getUsername())); }
}

@RestController
class HealthController {
    @GetMapping("/health") public ResponseEntity<String> health() { return ResponseEntity.ok("OK"); }
}
