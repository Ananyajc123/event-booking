package com.eventbooking.exception;
public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(String message) { super(message); }
}
