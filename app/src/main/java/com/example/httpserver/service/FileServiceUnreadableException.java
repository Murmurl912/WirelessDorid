package com.example.httpserver.service;

public class FileServiceUnreadableException extends FileServiceException {
    public FileServiceUnreadableException() {
        super();
    }

    public FileServiceUnreadableException(String message) {
        super(message);
    }

    public FileServiceUnreadableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceUnreadableException(Throwable cause) {
        super(cause);
    }

    protected FileServiceUnreadableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
