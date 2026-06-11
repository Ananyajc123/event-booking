package com.eventbooking.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name = "bookings")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String bookingReference;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "event_id", nullable = false) private Event event;
    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id"), inverseJoinColumns = @JoinColumn(name = "seat_id")) private List<Seat> seats;
    @Column(nullable = false) private BigDecimal totalAmount;
    @Column(nullable = false) @Enumerated(EnumType.STRING) private BookingStatus status = BookingStatus.PENDING;
    private LocalDateTime bookedAt;
    private LocalDateTime expiresAt;
    @PrePersist protected void onCreate() { bookedAt = LocalDateTime.now(); }
    public Booking() {}
    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getBookingReference() { return bookingReference; } public void setBookingReference(String v) { this.bookingReference = v; }
    public User getUser() { return user; } public void setUser(User v) { this.user = v; }
    public Event getEvent() { return event; } public void setEvent(Event v) { this.event = v; }
    public List<Seat> getSeats() { return seats; } public void setSeats(List<Seat> v) { this.seats = v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public BookingStatus getStatus() { return status; } public void setStatus(BookingStatus v) { this.status = v; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; } public void setExpiresAt(LocalDateTime v) { this.expiresAt = v; }
    public enum BookingStatus { PENDING, CONFIRMED, CANCELLED, EXPIRED }
    public static BookingBuilder builder() { return new BookingBuilder(); }
    public static class BookingBuilder {
        private String bookingReference; private User user; private Event event; private List<Seat> seats; private BigDecimal totalAmount; private BookingStatus status = BookingStatus.PENDING; private LocalDateTime expiresAt;
        public BookingBuilder bookingReference(String v) { this.bookingReference=v; return this; }
        public BookingBuilder user(User v) { this.user=v; return this; }
        public BookingBuilder event(Event v) { this.event=v; return this; }
        public BookingBuilder seats(List<Seat> v) { this.seats=v; return this; }
        public BookingBuilder totalAmount(BigDecimal v) { this.totalAmount=v; return this; }
        public BookingBuilder status(BookingStatus v) { this.status=v; return this; }
        public BookingBuilder expiresAt(LocalDateTime v) { this.expiresAt=v; return this; }
        public Booking build() { Booking b=new Booking(); b.bookingReference=bookingReference; b.user=user; b.event=event; b.seats=seats; b.totalAmount=totalAmount; b.status=status; b.expiresAt=expiresAt; return b; }
    }
}
