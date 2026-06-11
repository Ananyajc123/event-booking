package com.eventbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(), "message", message,
                "timestamp", LocalDateTime.now().toString()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<?> handleEventNotFound(EventNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({SeatNotAvailableException.class, EventNotAvailableException.class})
    public ResponseEntity<?> handleNotAvailable(RuntimeException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({BookingNotFoundException.class, SeatNotFoundException.class})
    public ResponseEntity<?> handleNotFound(RuntimeException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({InvalidBookingException.class, BookingExpiredException.class})
    public ResponseEntity<?> handleInvalid(RuntimeException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedBookingException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedBookingException e) {
        return error(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<?> handlePayment(PaymentFailedException e) {
        return error(HttpStatus.PAYMENT_REQUIRED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
