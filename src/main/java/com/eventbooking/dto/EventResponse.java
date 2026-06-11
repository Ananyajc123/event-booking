package com.eventbooking.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class EventResponse {
    private Long id; private String name, description, category, imageUrl, venueName, venueLocation; private LocalDateTime eventDate; private BigDecimal ticketPrice; private Integer availableSeats;
    public EventResponse() {}
    public Long getId() { return id; } public void setId(Long v) { this.id=v; }
    public String getName() { return name; } public void setName(String v) { this.name=v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description=v; }
    public String getCategory() { return category; } public void setCategory(String v) { this.category=v; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String v) { this.imageUrl=v; }
    public String getVenueName() { return venueName; } public void setVenueName(String v) { this.venueName=v; }
    public String getVenueLocation() { return venueLocation; } public void setVenueLocation(String v) { this.venueLocation=v; }
    public LocalDateTime getEventDate() { return eventDate; } public void setEventDate(LocalDateTime v) { this.eventDate=v; }
    public BigDecimal getTicketPrice() { return ticketPrice; } public void setTicketPrice(BigDecimal v) { this.ticketPrice=v; }
    public Integer getAvailableSeats() { return availableSeats; } public void setAvailableSeats(Integer v) { this.availableSeats=v; }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String name, description, category, imageUrl, venueName, venueLocation; private LocalDateTime eventDate; private BigDecimal ticketPrice; private Integer availableSeats;
        public Builder id(Long v) { this.id=v; return this; } public Builder name(String v) { this.name=v; return this; }
        public Builder description(String v) { this.description=v; return this; } public Builder category(String v) { this.category=v; return this; }
        public Builder imageUrl(String v) { this.imageUrl=v; return this; } public Builder venueName(String v) { this.venueName=v; return this; }
        public Builder venueLocation(String v) { this.venueLocation=v; return this; } public Builder eventDate(LocalDateTime v) { this.eventDate=v; return this; }
        public Builder ticketPrice(BigDecimal v) { this.ticketPrice=v; return this; } public Builder availableSeats(Integer v) { this.availableSeats=v; return this; }
        public EventResponse build() { EventResponse r=new EventResponse(); r.id=id; r.name=name; r.description=description; r.category=category; r.imageUrl=imageUrl; r.venueName=venueName; r.venueLocation=venueLocation; r.eventDate=eventDate; r.ticketPrice=ticketPrice; r.availableSeats=availableSeats; return r; }
    }
}
