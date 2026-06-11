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

## Interview Q&A

**Q: How do you prevent double booking?**
Two-layer approach: Redis distributed lock (SETNX) ensures only one transaction enters the critical section at a time. Inside, we use JPA PESSIMISTIC_WRITE (SELECT FOR UPDATE) which locks the DB row. If two transactions somehow pass Redis simultaneously, the DB lock ensures only one succeeds.

**Q: Why two layers? Isn't Redis enough?**
Redis is fast but not durable. If Redis goes down mid-transaction, we'd lose the lock. PostgreSQL is the source of truth. Redis prevents thundering herd; DB lock is the safety net.

**Q: Why pessimistic over optimistic locking?**
Optimistic locking retries on conflict — fine for low contention. For seat booking (high contention, last few seats), we want to fail fast and inform the user immediately rather than retry. Pessimistic locking blocks competing transactions upfront.

**Q: How does the 10-minute seat hold work?**
When seats are locked, we set Redis TTL = 600 seconds AND store expiresAt in DB. A scheduled job runs every 2 minutes, finds PENDING bookings past expiry, releases seats back to AVAILABLE. Redis TTL is the fast path; DB scheduler is the safety net.

**Q: How would you scale this to BookMyShow scale (10M users)?**
1. Redis Cluster for distributed locking across multiple nodes
2. PostgreSQL partitioning by event_id
3. Read replicas for seat map queries
4. Kafka for async booking confirmations
5. CDN for static seat map rendering
6. WebSocket for real-time seat availability updates
