package com.eventbooking.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class BookingConfirmResponse {
    private String bookingReference, status, eventName; private BigDecimal totalAmount; private LocalDateTime eventDate; private List<String> seats;
    public BookingConfirmResponse() {}
    public String getBookingReference() { return bookingReference; } public void setBookingReference(String v) { this.bookingReference=v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status=v; }
    public String getEventName() { return eventName; } public void setEventName(String v) { this.eventName=v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount=v; }
    public LocalDateTime getEventDate() { return eventDate; } public void setEventDate(LocalDateTime v) { this.eventDate=v; }
    public List<String> getSeats() { return seats; } public void setSeats(List<String> v) { this.seats=v; }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String bookingReference, status, eventName; private BigDecimal totalAmount; private LocalDateTime eventDate; private List<String> seats;
        public Builder bookingReference(String v) { this.bookingReference=v; return this; }
        public Builder status(String v) { this.status=v; return this; }
        public Builder eventName(String v) { this.eventName=v; return this; }
        public Builder totalAmount(BigDecimal v) { this.totalAmount=v; return this; }
        public Builder eventDate(LocalDateTime v) { this.eventDate=v; return this; }
        public Builder seats(List<String> v) { this.seats=v; return this; }
        public BookingConfirmResponse build() { BookingConfirmResponse r=new BookingConfirmResponse(); r.bookingReference=bookingReference; r.status=status; r.eventName=eventName; r.totalAmount=totalAmount; r.eventDate=eventDate; r.seats=seats; return r; }
    }
}
