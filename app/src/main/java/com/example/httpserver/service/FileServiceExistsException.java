package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceExistsException extends FileServiceException {

    public int code = 201;

    public FileServiceExistsException() {
    }

    public FileServiceExistsException(Path path) {
        super(path);
    }

    public FileServiceExistsException(String message, Path path) {
        super(message, path);
    }

    public FileServiceExistsException(String message) {
        super(message);
    }

    public FileServiceExistsException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceExistsException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceExistsException(Throwable cause) {
        super(cause);
    }

    public FileServiceExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    public FileServiceExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
