package com.example.CdnOriginServer.handler;

//Δημιουργία προσαρμοσμένης εξαίρεσης για την καλύτερη διαχείριση σφαλμάτων.

public class GlobalException {

    public static class FileConflictException extends RuntimeException {
        public FileConflictException(String message) {
            super(message);
        }
    }
}
