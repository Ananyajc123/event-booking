package com.eventbooking.model;
import jakarta.persistence.*;
import java.util.List;
@Entity @Table(name = "venues")
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String location;
    @Column(nullable = false) private Integer totalCapacity;
    @Column(nullable = false) private Integer rows;
    @Column(nullable = false) private Integer seatsPerRow;
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Event> events;
    public Venue() {}
    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getLocation() { return location; } public void setLocation(String v) { this.location = v; }
    public Integer getTotalCapacity() { return totalCapacity; } public void setTotalCapacity(Integer v) { this.totalCapacity = v; }
    public Integer getRows() { return rows; } public void setRows(Integer v) { this.rows = v; }
    public Integer getSeatsPerRow() { return seatsPerRow; } public void setSeatsPerRow(Integer v) { this.seatsPerRow = v; }
    public List<Event> getEvents() { return events; }
    public static VenueBuilder builder() { return new VenueBuilder(); }
    public static class VenueBuilder {
        private String name, location; private Integer totalCapacity, rows, seatsPerRow;
        public VenueBuilder name(String v) { this.name = v; return this; }
        public VenueBuilder location(String v) { this.location = v; return this; }
        public VenueBuilder totalCapacity(Integer v) { this.totalCapacity = v; return this; }
        public VenueBuilder rows(Integer v) { this.rows = v; return this; }
        public VenueBuilder seatsPerRow(Integer v) { this.seatsPerRow = v; return this; }
        public Venue build() { Venue v = new Venue(); v.name=name; v.location=location; v.totalCapacity=totalCapacity; v.rows=rows; v.seatsPerRow=seatsPerRow; return v; }
    }
}
