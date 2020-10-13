package com.example.httpserver.service;

public class FileServiceNotEmptyException extends FileServiceException {
    public FileServiceNotEmptyException() {
        super();
    }

    public FileServiceNotEmptyException(String message) {
        super(message);
    }

    public FileServiceNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceNotEmptyException(Throwable cause) {
        super(cause);
    }

    protected FileServiceNotEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
