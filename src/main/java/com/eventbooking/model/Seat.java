package com.eventbooking.model;
import jakarta.persistence.*;
@Entity @Table(name = "seats")
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String seatNumber;
    @Column(nullable = false) private String row;
    @Column(nullable = false) private Integer seatIndex;
    @Column(nullable = false) @Enumerated(EnumType.STRING) private SeatStatus status = SeatStatus.AVAILABLE;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "event_id", nullable = false) private Event event;
    @Version private Long version;
    public Seat() {}
    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getSeatNumber() { return seatNumber; } public void setSeatNumber(String v) { this.seatNumber = v; }
    public String getRow() { return row; } public void setRow(String v) { this.row = v; }
    public Integer getSeatIndex() { return seatIndex; } public void setSeatIndex(Integer v) { this.seatIndex = v; }
    public SeatStatus getStatus() { return status; } public void setStatus(SeatStatus v) { this.status = v; }
    public Event getEvent() { return event; } public void setEvent(Event v) { this.event = v; }
    public enum SeatStatus { AVAILABLE, LOCKED, BOOKED }
    public static SeatBuilder builder() { return new SeatBuilder(); }
    public static class SeatBuilder {
        private String seatNumber, row; private Integer seatIndex; private Event event; private SeatStatus status = SeatStatus.AVAILABLE;
        public SeatBuilder seatNumber(String v) { this.seatNumber=v; return this; }
        public SeatBuilder row(String v) { this.row=v; return this; }
        public SeatBuilder seatIndex(Integer v) { this.seatIndex=v; return this; }
        public SeatBuilder event(Event v) { this.event=v; return this; }
        public SeatBuilder status(SeatStatus v) { this.status=v; return this; }
        public Seat build() { Seat s=new Seat(); s.seatNumber=seatNumber; s.row=row; s.seatIndex=seatIndex; s.event=event; s.status=status; return s; }
    }
}
