# 🎭 BookIt — Event Booking System

Production-grade event booking system with distributed locking, concurrency-safe seat reservations, and real-time seat maps.

## Live Demo
> Deploy link here (Render)

---

## Architecture

```
React Frontend (Port 3000)
        │
        ▼
Spring Boot REST API (Port 8081)
        │
   ┌────┴────┐
   │         │
Redis      PostgreSQL
(Distributed  (Events, Seats,
 Locking)      Bookings)
```

---

## The Core Problem: Concurrent Seat Booking

**Scenario:** 500 users click "Book Seat A1" at the exact same millisecond.

**Without locking:**
- All 500 read status = AVAILABLE
- All 500 write status = LOCKED
- Result: 500 bookings for 1 seat 💥

**Our Solution — Two-layer locking:**

```
Layer 1: Redis Distributed Lock (SET key NX EX 30)
  └── Only ONE transaction enters the critical section at a time

Layer 2: PostgreSQL Pessimistic Lock (SELECT FOR UPDATE)
  └── Database-level guarantee — no dirty reads
```

---

## Features

- 🔒 **Distributed locking** — Redis SETNX + DB pessimistic lock (PESSIMISTIC_WRITE)
- ⏱️ **Seat hold** — 10-minute timer while user completes payment
- 🗺️ **Visual seat map** — real-time availability (Available/Locked/Booked)
- 📅 **Event management** — categories, search, date filtering
- 🔄 **Auto-release** — expired bookings cleaned up every 2 minutes (scheduled job)
- 💳 **Payment simulation** — 95% success rate
- 🧪 **6 unit tests** — covering concurrency scenarios

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL (persistent) |
| Distributed Lock | Redis (SETNX) |
| DB-level Lock | JPA PESSIMISTIC_WRITE |
| Auth | Spring Security + JWT |
| Frontend | React 18 |
| Testing | JUnit 5, Mockito |

---

## Setup & Run

### Prerequisites
- Java 17+, PostgreSQL, Redis, Node.js 18+

### 1. Create database
```sql
CREATE DATABASE eventbooking;
```

### 2. Start Redis (Mac)
```bash
brew services start redis
```

### 3. Start backend
```bash
./mvnw spring-boot:run
```
App auto-seeds 4 events + demo users on first run.

### 4. Start frontend
```bash
cd frontend && npm install && npm start
```

### Demo credentials
- User: `user@demo.com` / `user123`
- Admin: `admin@demo.com` / `admin123`

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/auth/register | No | Register |
| POST | /api/auth/login | No | Login |
| GET | /api/events | No | List events |
| GET | /api/events/{id}/seats | No | Seat map |
| POST | /api/bookings/init | Yes | Lock seats |
| POST | /api/bookings/{ref}/confirm | Yes | Confirm payment |
| DELETE | /api/bookings/{ref} | Yes | Cancel |
| GET | /api/bookings/my | Yes | My bookings |

---


