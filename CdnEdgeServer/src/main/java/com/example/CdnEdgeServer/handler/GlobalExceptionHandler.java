package com.example.CdnEdgeServer.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.io.FileNotFoundException;

/*
Το GlobalExceptionHandler αποτελεί το κεντρικό σημείο διαχείρισης σφαλμάτων της εφαρμογής.

Μετατρέπει συγκεκριμένα exceptions σε κατάλληλες HTTP αποκρίσεις ώστε ο client να λαμβάνει
συνεπή και κατανοητά μηνύματα σφάλματος.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFound(FileNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleRestClientResponse(RestClientResponseException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body("Origin error: " + ex.getResponseBodyAsString());
    }
}