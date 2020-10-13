package com.example.httpserver.service;

public class FileServiceIsDirectoryException extends FileServiceException {
    public FileServiceIsDirectoryException() {
        super();
    }

    public FileServiceIsDirectoryException(String message) {
        super(message);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceIsDirectoryException(Throwable cause) {
        super(cause);
    }

    protected FileServiceIsDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
