package com.eventbooking.dto;
import java.util.List;
public class SeatMapResponse {
    private Long eventId; private String eventName; private Integer totalSeats, availableSeats; private List<SeatDTO> seats;
    public SeatMapResponse() {}
    public Long getEventId() { return eventId; } public void setEventId(Long v) { this.eventId=v; }
    public String getEventName() { return eventName; } public void setEventName(String v) { this.eventName=v; }
    public Integer getTotalSeats() { return totalSeats; } public void setTotalSeats(Integer v) { this.totalSeats=v; }
    public Integer getAvailableSeats() { return availableSeats; } public void setAvailableSeats(Integer v) { this.availableSeats=v; }
    public List<SeatDTO> getSeats() { return seats; } public void setSeats(List<SeatDTO> v) { this.seats=v; }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long eventId; private String eventName; private Integer totalSeats, availableSeats; private List<SeatDTO> seats;
        public Builder eventId(Long v) { this.eventId=v; return this; } public Builder eventName(String v) { this.eventName=v; return this; }
        public Builder totalSeats(Integer v) { this.totalSeats=v; return this; } public Builder availableSeats(Integer v) { this.availableSeats=v; return this; }
        public Builder seats(List<SeatDTO> v) { this.seats=v; return this; }
        public SeatMapResponse build() { SeatMapResponse r=new SeatMapResponse(); r.eventId=eventId; r.eventName=eventName; r.totalSeats=totalSeats; r.availableSeats=availableSeats; r.seats=seats; return r; }
    }
    public static class SeatDTO {
        private Long id; private String seatNumber, row, status; private Integer seatIndex;
        public SeatDTO() {}
        public SeatDTO(Long id, String seatNumber, String row, Integer seatIndex, String status) { this.id=id; this.seatNumber=seatNumber; this.row=row; this.seatIndex=seatIndex; this.status=status; }
        public Long getId() { return id; } public String getSeatNumber() { return seatNumber; }
        public String getRow() { return row; } public String getStatus() { return status; }
        public Integer getSeatIndex() { return seatIndex; }
    }
}
