package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceUnreadableException extends FileServiceException {
    public int code = 701;
    public FileServiceUnreadableException() {
        super();
    }

    public FileServiceUnreadableException(Path path) {
        super(path);
    }

    public FileServiceUnreadableException(String message, Path path) {
        super(message, path);
    }

    public FileServiceUnreadableException(String message) {
        super(message);
    }

    public FileServiceUnreadableException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceUnreadableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceUnreadableException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceUnreadableException(Throwable cause) {
        super(cause);
    }

    public FileServiceUnreadableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceUnreadableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
