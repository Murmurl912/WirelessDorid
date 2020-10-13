package com.example.httpserver.service;

public class FileServiceException extends RuntimeException {

    public FileServiceException() {
        super();
    }

    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceException(Throwable cause) {
        super(cause);
    }

    protected FileServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
