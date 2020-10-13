package com.example.httpserver.service;

public class FileServiceUnwritableException extends FileServiceException {
    public FileServiceUnwritableException() {
        super();
    }

    public FileServiceUnwritableException(String message) {
        super(message);
    }

    public FileServiceUnwritableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceUnwritableException(Throwable cause) {
        super(cause);
    }

    protected FileServiceUnwritableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
