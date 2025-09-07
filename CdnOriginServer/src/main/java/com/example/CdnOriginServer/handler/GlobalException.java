package com.example.CdnOriginServer.handler;

public class GlobalException {

    public static class FileConflictException extends RuntimeException {
        public FileConflictException(String message) {
            super(message);
        }
    }
}
