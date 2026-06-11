package com.eventbooking.dto;
import jakarta.validation.constraints.*;
import java.util.List;
public class BookingInitRequest {
    @NotNull private Long eventId;
    @NotEmpty @Size(min=1, max=10) private List<Long> seatIds;
    public BookingInitRequest() {}
    public BookingInitRequest(Long eventId, List<Long> seatIds) { this.eventId=eventId; this.seatIds=seatIds; }
    public Long getEventId() { return eventId; } public void setEventId(Long v) { this.eventId=v; }
    public List<Long> getSeatIds() { return seatIds; } public void setSeatIds(List<Long> v) { this.seatIds=v; }
}
