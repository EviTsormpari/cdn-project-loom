package com.example.CdnOriginServer.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

/*
Το GlobalExceptionHandler αποτελεί το κεντρικό σημείο διαχείρισης σφαλμάτων της εφαρμογής.

Μετατρέπει συγκεκριμένα exceptions σε κατάλληλες HTTP αποκρίσεις ώστε να λαμβάνονται
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(GlobalException.FileConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}