import React, { useState, useEffect, useCallback } from "react";
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useParams } from "react-router-dom";
import { login, register, getEvents, getSeatMap, initBooking, confirmBooking, getMyBookings } from "./services/api";

// ─── Auth Page ────────────────────────────────────────────────────────────────
function Auth({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true);
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handle = async (e) => {
    e.preventDefault(); setLoading(true); setError("");
    try {
      const res = isLogin ? await login(form) : await register(form);
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("userName", res.data.name);
      onLogin();
    } catch (err) { setError(err.response?.data?.message || "Failed"); }
    finally { setLoading(false); }
  };

  return (
    <div style={S.center}>
      <div style={S.authCard}>
        <h1 style={{ textAlign: "center", margin: "0 0 4px" }}>🎭 BookIt</h1>
        <p style={{ textAlign: "center", color: "#888", marginBottom: 24, fontSize: 13 }}>Your event booking platform</p>
        <div style={S.tabs}>
          {["Login", "Register"].map((t, i) => (
            <button key={t} style={isLogin === !i ? S.activeTab : S.tab} onClick={() => setIsLogin(!i)}>{t}</button>
          ))}
        </div>
        <form onSubmit={handle} style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          {!isLogin && <input style={S.input} placeholder="Full Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />}
          <input style={S.input} placeholder="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required />
          <input style={S.input} placeholder="Password" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} required />
          {error && <p style={{ color: "#e53e3e", fontSize: 13, margin: 0 }}>{error}</p>}
          <button style={S.btn} disabled={loading}>{loading ? "..." : isLogin ? "Login" : "Create Account"}</button>
        </form>
        <p style={{ fontSize: 12, color: "#aaa", textAlign: "center", marginTop: 12 }}>Demo: user@demo.com / user123</p>
      </div>
    </div>
  );
}

// ─── Event Card ───────────────────────────────────────────────────────────────
function EventCard({ event, onClick }) {
  return (
    <div style={S.eventCard} onClick={onClick}>
      <img src={event.imageUrl} alt={event.name} style={{ width: "100%", height: 160, objectFit: "cover", borderRadius: "12px 12px 0 0" }} onError={e => e.target.src = "https://picsum.photos/400/200?random=99"} />
      <div style={{ padding: 16 }}>
        <span style={S.categoryBadge}>{event.category}</span>
        <h3 style={{ margin: "8px 0 4px", fontSize: 16 }}>{event.name}</h3>
        <p style={{ color: "#888", fontSize: 13, margin: "0 0 8px" }}>📍 {event.venueName} · {event.venueLocation}</p>
        <p style={{ color: "#888", fontSize: 13, margin: "0 0 12px" }}>📅 {new Date(event.eventDate).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" })}</p>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <span style={{ fontWeight: 700, color: "#4f46e5", fontSize: 18 }}>₹{event.ticketPrice}</span>
          <span style={{ fontSize: 12, color: event.availableSeats > 0 ? "#10b981" : "#e53e3e" }}>
            {event.availableSeats > 0 ? `${event.availableSeats} seats left` : "SOLD OUT"}
          </span>
        </div>
      </div>
    </div>
  );
}

// ─── Events Page ──────────────────────────────────────────────────────────────
function EventsPage({ onLogout }) {
  const navigate = useNavigate();
  const [events, setEvents] = useState([]);
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [loading, setLoading] = useState(true);
  const userName = localStorage.getItem("userName");

  const fetchEvents = useCallback(async () => {
    setLoading(true);
    try {
      const params = {};
      if (search) params.search = search;
      else if (category) params.category = category;
      const res = await getEvents(params);
      setEvents(res.data);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }, [search, category]);

  useEffect(() => { fetchEvents(); }, [fetchEvents]);

  return (
    <div style={S.page}>
      <nav style={S.nav}>
        <h2 style={{ margin: 0 }}>🎭 BookIt</h2>
        <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
          <span style={{ fontSize: 14, color: "#555" }}>Hi, {userName}</span>
          <button style={S.outlineBtn} onClick={() => navigate("/bookings")}>My Bookings</button>
          <button style={S.outlineBtn} onClick={onLogout}>Logout</button>
        </div>
      </nav>
      <div style={{ maxWidth: 1100, margin: "0 auto", padding: "24px 16px" }}>
        <h2 style={{ marginBottom: 20 }}>Upcoming Events</h2>
        <div style={{ display: "flex", gap: 12, marginBottom: 24, flexWrap: "wrap" }}>
          <input style={{ ...S.input, flex: 1, minWidth: 200 }} placeholder="🔍 Search events..." value={search} onChange={e => { setSearch(e.target.value); setCategory(""); }} />
          {["", "MUSIC", "SPORTS", "COMEDY", "THEATRE"].map(c => (
            <button key={c} style={category === c ? S.activeTab : S.tab} onClick={() => { setCategory(c); setSearch(""); }}>
              {c || "All"}
            </button>
          ))}
        </div>
        {loading ? <p style={{ textAlign: "center", color: "#aaa" }}>Loading events...</p>
          : events.length === 0 ? <p style={{ textAlign: "center", color: "#aaa" }}>No events found</p>
          : <div style={S.eventGrid}>
              {events.map(e => <EventCard key={e.id} event={e} onClick={() => navigate(`/events/${e.id}`)} />)}
            </div>}
      </div>
    </div>
  );
}

// ─── Seat Map + Booking Page ──────────────────────────────────────────────────
function BookingPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [seatMap, setSeatMap] = useState(null);
  const [selected, setSelected] = useState([]);
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [step, setStep] = useState("select"); // select | confirm | success

  useEffect(() => {
    getSeatMap(id).then(r => setSeatMap(r.data)).catch(console.error);
  }, [id]);

  const toggleSeat = (seat) => {
    if (seat.status !== "AVAILABLE") return;
    setSelected(prev =>
      prev.find(s => s.id === seat.id)
        ? prev.filter(s => s.id !== seat.id)
        : [...prev, seat]
    );
  };

  const handleInitBooking = async () => {
    if (selected.length === 0) return;
    setLoading(true); setError("");
    try {
      const res = await initBooking({ eventId: Number(id), seatIds: selected.map(s => s.id) });
      setBooking(res.data);
      setStep("confirm");
    } catch (err) { setError(err.response?.data?.message || "Failed to lock seats"); }
    finally { setLoading(false); }
  };

  const handleConfirm = async () => {
    setLoading(true); setError("");
    try {
      await confirmBooking(booking.bookingReference);
      setStep("success");
    } catch (err) { setError(err.response?.data?.message || "Payment failed"); }
    finally { setLoading(false); }
  };

  if (!seatMap) return <div style={S.center}><p>Loading seat map...</p></div>;

  const rows = [...new Set(seatMap.seats.map(s => s.row))].sort();

  const seatColor = (seat) => {
    if (selected.find(s => s.id === seat.id)) return "#4f46e5";
    if (seat.status === "BOOKED") return "#e53e3e";
    if (seat.status === "LOCKED") return "#f59e0b";
    return "#e2e8f0";
  };

  return (
    <div style={S.page}>
      <nav style={S.nav}>
        <button style={S.outlineBtn} onClick={() => navigate(-1)}>← Back</button>
        <h2 style={{ margin: 0 }}>{seatMap.eventName}</h2>
        <span style={{ fontSize: 14, color: "#10b981" }}>{seatMap.availableSeats} seats available</span>
      </nav>

      <div style={{ maxWidth: 800, margin: "24px auto", padding: "0 16px" }}>
        {step === "select" && (
          <>
            {/* Legend */}
            <div style={{ display: "flex", gap: 20, marginBottom: 20, justifyContent: "center", flexWrap: "wrap" }}>
              {[["#e2e8f0", "Available"], ["#4f46e5", "Selected"], ["#f59e0b", "Locked"], ["#e53e3e", "Booked"]].map(([color, label]) => (
                <div key={label} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <div style={{ width: 20, height: 20, background: color, borderRadius: 4 }} />
                  <span style={{ fontSize: 13 }}>{label}</span>
                </div>
              ))}
            </div>

            {/* Stage */}
            <div style={{ background: "#1a1a2e", color: "white", textAlign: "center", padding: "10px", borderRadius: 8, marginBottom: 24, fontSize: 13, letterSpacing: 2 }}>STAGE</div>

            {/* Seat Map */}
            {rows.map(row => (
              <div key={row} style={{ display: "flex", gap: 8, marginBottom: 8, alignItems: "center", justifyContent: "center" }}>
                <span style={{ width: 20, fontWeight: 700, fontSize: 13, color: "#888" }}>{row}</span>
                {seatMap.seats.filter(s => s.row === row).sort((a, b) => a.seatIndex - b.seatIndex).map(seat => (
                  <div key={seat.id}
                    onClick={() => toggleSeat(seat)}
                    title={seat.seatNumber}
                    style={{ width: 32, height: 32, borderRadius: 6, background: seatColor(seat),
                      cursor: seat.status === "AVAILABLE" ? "pointer" : "not-allowed",
                      display: "flex", alignItems: "center", justifyContent: "center",
                      fontSize: 10, color: selected.find(s => s.id === seat.id) ? "white" : "#333",
                      transition: "transform 0.1s", transform: selected.find(s => s.id === seat.id) ? "scale(1.1)" : "scale(1)" }}>
                    {seat.seatIndex}
                  </div>
                ))}
              </div>
            ))}

            {selected.length > 0 && (
              <div style={{ ...S.card, marginTop: 24, textAlign: "center" }}>
                <p style={{ fontSize: 15 }}><b>{selected.length}</b> seat(s) selected: {selected.map(s => s.seatNumber).join(", ")}</p>
                {error && <p style={{ color: "#e53e3e", fontSize: 13 }}>{error}</p>}
                <button style={{ ...S.btn, marginTop: 12 }} onClick={handleInitBooking} disabled={loading}>
                  {loading ? "Locking seats..." : `Proceed to Pay`}
                </button>
              </div>
            )}
          </>
        )}

        {step === "confirm" && booking && (
          <div style={{ ...S.card, textAlign: "center" }}>
            <h3>⏳ Complete Your Booking</h3>
            <p style={{ color: "#888", fontSize: 14 }}>Seats locked for 10 minutes</p>
            <div style={{ background: "#f8faff", borderRadius: 10, padding: 20, margin: "16px 0" }}>
              <p><b>Ref:</b> {booking.bookingReference}</p>
              <p><b>Seats:</b> {booking.seats?.join(", ")}</p>
              <p><b>Total:</b> ₹{booking.totalAmount}</p>
              <p style={{ color: "#f59e0b", fontSize: 13 }}>
                ⚠️ Expires at {new Date(booking.expiresAt).toLocaleTimeString()}
              </p>
            </div>
            {error && <p style={{ color: "#e53e3e", fontSize: 13 }}>{error}</p>}
            <button style={S.btn} onClick={handleConfirm} disabled={loading}>
              {loading ? "Processing payment..." : "💳 Confirm & Pay"}
            </button>
          </div>
        )}

        {step === "success" && (
          <div style={{ ...S.card, textAlign: "center" }}>
            <div style={{ fontSize: 60 }}>✅</div>
            <h2>Booking Confirmed!</h2>
            <p style={{ color: "#888" }}>Ref: <b>{booking?.bookingReference}</b></p>
            <p>Seats: <b>{booking?.seats?.join(", ")}</b></p>
            <div style={{ display: "flex", gap: 12, justifyContent: "center", marginTop: 20 }}>
              <button style={S.btn} onClick={() => navigate("/bookings")}>View My Bookings</button>
              <button style={S.outlineBtn} onClick={() => navigate("/")}>Browse More</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ─── My Bookings Page ─────────────────────────────────────────────────────────
function MyBookings() {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    getMyBookings().then(r => setBookings(r.data)).catch(console.error);
  }, []);

  const statusColor = { CONFIRMED: "#10b981", PENDING: "#f59e0b", CANCELLED: "#e53e3e", EXPIRED: "#aaa" };

  return (
    <div style={S.page}>
      <nav style={S.nav}>
        <button style={S.outlineBtn} onClick={() => navigate("/")}>← Events</button>
        <h2 style={{ margin: 0 }}>My Bookings</h2>
        <div />
      </nav>
      <div style={{ maxWidth: 800, margin: "24px auto", padding: "0 16px" }}>
        {bookings.length === 0
          ? <div style={{ ...S.card, textAlign: "center" }}><p style={{ color: "#aaa" }}>No bookings yet</p><button style={S.btn} onClick={() => navigate("/")}>Browse Events</button></div>
          : bookings.map(b => (
            <div key={b.bookingReference} style={S.card}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                <div>
                  <h3 style={{ margin: "0 0 4px" }}>{b.eventName}</h3>
                  <p style={{ color: "#888", fontSize: 13, margin: "0 0 8px" }}>
                    📅 {new Date(b.eventDate).toLocaleDateString("en-IN", { dateStyle: "medium" })}
                  </p>
                  <p style={{ fontSize: 13, margin: "0 0 4px" }}>🪑 Seats: <b>{b.seats?.join(", ")}</b></p>
                  <p style={{ fontSize: 13, margin: 0 }}>Ref: <code>{b.bookingReference}</code></p>
                </div>
                <div style={{ textAlign: "right" }}>
                  <span style={{ background: statusColor[b.status] + "22", color: statusColor[b.status], padding: "4px 12px", borderRadius: 20, fontSize: 12, fontWeight: 600 }}>{b.status}</span>
                  <p style={{ fontWeight: 700, fontSize: 18, color: "#4f46e5", margin: "8px 0 0" }}>₹{b.totalAmount}</p>
                </div>
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  const [auth, setAuth] = useState(!!localStorage.getItem("token"));

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userName");
    setAuth(false);
  };

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={auth ? <Navigate to="/" /> : <Auth onLogin={() => setAuth(true)} />} />
        <Route path="/" element={auth ? <EventsPage onLogout={handleLogout} /> : <Navigate to="/login" />} />
        <Route path="/events/:id" element={auth ? <BookingPage /> : <Navigate to="/login" />} />
        <Route path="/bookings" element={auth ? <MyBookings /> : <Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

// ─── Styles ───────────────────────────────────────────────────────────────────
const S = {
  page: { minHeight: "100vh", background: "#f0f4ff", fontFamily: "system-ui, sans-serif" },
  center: { minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#f0f4ff" },
  nav: { background: "white", padding: "16px 32px", display: "flex", justifyContent: "space-between", alignItems: "center", boxShadow: "0 1px 4px rgba(0,0,0,0.08)", position: "sticky", top: 0, zIndex: 10 },
  authCard: { background: "white", borderRadius: 16, padding: 40, width: 380, boxShadow: "0 4px 24px rgba(0,0,0,0.08)" },
  card: { background: "white", borderRadius: 16, padding: 24, marginBottom: 16, boxShadow: "0 2px 12px rgba(0,0,0,0.06)" },
  eventGrid: { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 20 },
  eventCard: { background: "white", borderRadius: 12, boxShadow: "0 2px 12px rgba(0,0,0,0.06)", cursor: "pointer", transition: "transform 0.15s, box-shadow 0.15s", overflow: "hidden" },
  input: { padding: "10px 14px", borderRadius: 8, border: "1px solid #e0e0e0", fontSize: 14, outline: "none", width: "100%", boxSizing: "border-box" },
  btn: { padding: "12px 24px", borderRadius: 8, background: "#4f46e5", color: "white", border: "none", cursor: "pointer", fontWeight: 600, fontSize: 14, width: "100%" },
  outlineBtn: { padding: "8px 16px", borderRadius: 8, background: "none", border: "1px solid #e0e0e0", cursor: "pointer", fontSize: 13 },
  tabs: { display: "flex", marginBottom: 20, borderRadius: 8, overflow: "hidden", border: "1px solid #e0e0e0" },
  tab: { flex: 1, padding: "8px 16px", border: "none", background: "white", cursor: "pointer", fontSize: 13 },
  activeTab: { flex: 1, padding: "8px 16px", border: "none", background: "#4f46e5", color: "white", cursor: "pointer", fontSize: 13, fontWeight: 600 },
  categoryBadge: { background: "#ede9fe", color: "#4f46e5", padding: "3px 10px", borderRadius: 20, fontSize: 11, fontWeight: 600 },
};
