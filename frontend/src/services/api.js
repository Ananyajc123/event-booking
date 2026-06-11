import axios from 'axios';

const API = axios.create({ baseURL: '/api' });

API.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

API.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const login = (data) => API.post('/auth/login', data);
export const register = (data) => API.post('/auth/register', data);
export const getEvents = (params) => API.get('/events', { params });
export const getEvent = (id) => API.get(`/events/${id}`);
export const getSeatMap = (id) => API.get(`/events/${id}/seats`);
export const initBooking = (data) => API.post('/bookings/init', data);
export const confirmBooking = (ref) => API.post(`/bookings/${ref}/confirm`);
export const cancelBooking = (ref) => API.delete(`/bookings/${ref}`);
export const getMyBookings = () => API.get('/bookings/my');
