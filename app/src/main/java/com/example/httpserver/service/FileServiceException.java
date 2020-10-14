package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceException extends RuntimeException {

    public final int code = 101;

    public Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public FileServiceException() {
        super();
    }

    public FileServiceException(Path path) {
        this.path = path;
    }

    public FileServiceException(String message, Path path) {
        super(message);
        this.path = path;
    }

    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(String message, Throwable cause, Path path) {
        super(message, cause);
        this.path = path;
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceException(Throwable cause, Path path) {
        super(cause);
        this.path = path;
    }

    public FileServiceException(Throwable cause) {
        super(cause);
    }
    public FileServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.path = path;
    }

    public FileServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
