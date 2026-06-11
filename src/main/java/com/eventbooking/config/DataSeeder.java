package com.eventbooking.config;

import com.eventbooking.model.*;
import com.eventbooking.repository.*;
import com.eventbooking.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (venueRepository.count() > 0) return;
        log.info("Seeding sample data...");

        Venue venue1 = new Venue();
        venue1.setName("Bangalore Palace Grounds");
        venue1.setLocation("Bangalore, Karnataka");
        venue1.setTotalCapacity(50);
        venue1.setRows(5);
        venue1.setSeatsPerRow(10);
        venueRepository.save(venue1);

        Venue venue2 = new Venue();
        venue2.setName("NIMHANS Convention Centre");
        venue2.setLocation("Bangalore, Karnataka");
        venue2.setTotalCapacity(30);
        venue2.setRows(3);
        venue2.setSeatsPerRow(10);
        venueRepository.save(venue2);

        Event e1 = new Event();
        e1.setName("Arijit Singh Live");
        e1.setDescription("The soulful voice of Bollywood live in concert");
        e1.setCategory("MUSIC");
        e1.setEventDate(LocalDateTime.now().plusDays(30));
        e1.setTicketPrice(new BigDecimal("1499"));
        e1.setImageUrl("https://picsum.photos/400/200?random=1");
        e1.setVenue(venue1);
        eventService.createEvent(e1);

        Event e2 = new Event();
        e2.setName("IPL: RCB vs MI");
        e2.setDescription("Royal Challengers vs Mumbai Indians T20 thriller");
        e2.setCategory("SPORTS");
        e2.setEventDate(LocalDateTime.now().plusDays(15));
        e2.setTicketPrice(new BigDecimal("799"));
        e2.setImageUrl("https://picsum.photos/400/200?random=2");
        e2.setVenue(venue2);
        eventService.createEvent(e2);

        Event e3 = new Event();
        e3.setName("Zakir Khan Stand-Up");
        e3.setDescription("Sakht Launda Live Comedy Special");
        e3.setCategory("COMEDY");
        e3.setEventDate(LocalDateTime.now().plusDays(10));
        e3.setTicketPrice(new BigDecimal("599"));
        e3.setImageUrl("https://picsum.photos/400/200?random=3");
        e3.setVenue(venue1);
        eventService.createEvent(e3);

        Event e4 = new Event();
        e4.setName("Hamlet Drama");
        e4.setDescription("Shakespeare masterpiece performed live");
        e4.setCategory("THEATRE");
        e4.setEventDate(LocalDateTime.now().plusDays(20));
        e4.setTicketPrice(new BigDecimal("999"));
        e4.setImageUrl("https://picsum.photos/400/200?random=4");
        e4.setVenue(venue2);
        eventService.createEvent(e4);

        if (!userRepository.existsByEmail("admin@demo.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@demo.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("user@demo.com")) {
            User user = new User();
            user.setName("Ananya");
            user.setEmail("user@demo.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            userRepository.save(user);
        }

        log.info("Sample data seeded successfully");
    }
}
