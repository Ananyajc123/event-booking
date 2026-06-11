package com.eventbooking.exception;
public class UnauthorizedBookingException extends RuntimeException {
    public UnauthorizedBookingException(String message) { super(message); }
}
