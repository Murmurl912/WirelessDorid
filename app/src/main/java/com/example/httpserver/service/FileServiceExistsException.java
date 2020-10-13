package com.example.httpserver.service;

public class FileServiceExistsException extends FileServiceException {
    public FileServiceExistsException() {
        super();
    }

    public FileServiceExistsException(String message) {
        super(message);
    }

    public FileServiceExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceExistsException(Throwable cause) {
        super(cause);
    }

    protected FileServiceExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
