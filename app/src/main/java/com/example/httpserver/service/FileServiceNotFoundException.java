package com.example.httpserver.service;

public class FileServiceNotFoundException extends FileServiceException {
    public FileServiceNotFoundException() {
        super();
    }

    public FileServiceNotFoundException(String message) {
        super(message);
    }

    public FileServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    protected FileServiceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
