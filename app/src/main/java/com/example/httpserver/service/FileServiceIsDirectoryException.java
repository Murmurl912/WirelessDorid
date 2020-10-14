package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceIsDirectoryException extends FileServiceException {
    public int code = 301;

    public FileServiceIsDirectoryException() {
        super();
    }

    public FileServiceIsDirectoryException(Path path) {
        super(path);
    }

    public FileServiceIsDirectoryException(String message, Path path) {
        super(message, path);
    }

    public FileServiceIsDirectoryException(String message) {
        super(message);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceIsDirectoryException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceIsDirectoryException(Throwable cause) {
        super(cause);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceIsDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
