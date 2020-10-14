package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceNotEmptyException extends FileServiceException {
    public int code = 501;
    public FileServiceNotEmptyException() {
        super();
    }

    public FileServiceNotEmptyException(Path path) {
        super(path);
    }

    public FileServiceNotEmptyException(String message, Path path) {
        super(message, path);
    }

    public FileServiceNotEmptyException(String message) {
        super(message);
    }

    public FileServiceNotEmptyException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceNotEmptyException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceNotEmptyException(Throwable cause) {
        super(cause);
    }

    public FileServiceNotEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceNotEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
