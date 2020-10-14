package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceNotFoundException extends FileServiceException {
    public int code = 601;
    public FileServiceNotFoundException() {
        super();
    }

    public FileServiceNotFoundException(Path path) {
        super(path);
    }

    public FileServiceNotFoundException(String message, Path path) {
        super(message, path);
    }

    public FileServiceNotFoundException(String message) {
        super(message);
    }

    public FileServiceNotFoundException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceNotFoundException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceNotFoundException(Throwable cause) {
        super(cause);
    }

    public FileServiceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
