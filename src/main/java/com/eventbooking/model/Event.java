package com.eventbooking.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity @Table(name = "events")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 1000) private String description;
    @Column(nullable = false) private String category;
    @Column(nullable = false) private LocalDateTime eventDate;
    @Column(nullable = false) private BigDecimal ticketPrice;
    @Column(nullable = false) private String imageUrl;
    @Column(nullable = false) private Boolean isActive = true;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "venue_id", nullable = false) private Venue venue;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Seat> seats;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Booking> bookings;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Event() {}
    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getCategory() { return category; } public void setCategory(String v) { this.category = v; }
    public LocalDateTime getEventDate() { return eventDate; } public void setEventDate(LocalDateTime v) { this.eventDate = v; }
    public BigDecimal getTicketPrice() { return ticketPrice; } public void setTicketPrice(BigDecimal v) { this.ticketPrice = v; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String v) { this.imageUrl = v; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean v) { this.isActive = v; }
    public Venue getVenue() { return venue; } public void setVenue(Venue v) { this.venue = v; }
    public List<Seat> getSeats() { return seats; }
    public List<Booking> getBookings() { return bookings; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public static EventBuilder builder() { return new EventBuilder(); }
    public static class EventBuilder {
        private String name, description, category, imageUrl; private BigDecimal ticketPrice; private LocalDateTime eventDate; private Venue venue; private Boolean isActive = true;
        public EventBuilder name(String v) { this.name=v; return this; }
        public EventBuilder description(String v) { this.description=v; return this; }
        public EventBuilder category(String v) { this.category=v; return this; }
        public EventBuilder imageUrl(String v) { this.imageUrl=v; return this; }
        public EventBuilder ticketPrice(BigDecimal v) { this.ticketPrice=v; return this; }
        public EventBuilder eventDate(LocalDateTime v) { this.eventDate=v; return this; }
        public EventBuilder venue(Venue v) { this.venue=v; return this; }
        public EventBuilder isActive(Boolean v) { this.isActive=v; return this; }
        public Event build() { Event e=new Event(); e.name=name; e.description=description; e.category=category; e.imageUrl=imageUrl; e.ticketPrice=ticketPrice; e.eventDate=eventDate; e.venue=venue; e.isActive=isActive; return e; }
    }
}
