package com.eventbooking.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public class BookingInitResponse {
    private String bookingReference; private BigDecimal totalAmount; private LocalDateTime expiresAt; private List<String> seats;
    public BookingInitResponse() {}
    public String getBookingReference() { return bookingReference; } public void setBookingReference(String v) { this.bookingReference=v; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount=v; }
    public LocalDateTime getExpiresAt() { return expiresAt; } public void setExpiresAt(LocalDateTime v) { this.expiresAt=v; }
    public List<String> getSeats() { return seats; } public void setSeats(List<String> v) { this.seats=v; }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String bookingReference; private BigDecimal totalAmount; private LocalDateTime expiresAt; private List<String> seats;
        public Builder bookingReference(String v) { this.bookingReference=v; return this; }
        public Builder totalAmount(BigDecimal v) { this.totalAmount=v; return this; }
        public Builder expiresAt(LocalDateTime v) { this.expiresAt=v; return this; }
        public Builder seats(List<String> v) { this.seats=v; return this; }
        public BookingInitResponse build() { BookingInitResponse r=new BookingInitResponse(); r.bookingReference=bookingReference; r.totalAmount=totalAmount; r.expiresAt=expiresAt; r.seats=seats; return r; }
    }
}
