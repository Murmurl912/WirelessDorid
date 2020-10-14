package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceUnwritableException extends FileServiceException {
    public int code = 801;
    public FileServiceUnwritableException() {
        super();
    }

    public FileServiceUnwritableException(Path path) {
        super(path);
    }

    public FileServiceUnwritableException(String message, Path path) {
        super(message, path);
    }

    public FileServiceUnwritableException(String message) {
        super(message);
    }

    public FileServiceUnwritableException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceUnwritableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceUnwritableException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceUnwritableException(Throwable cause) {
        super(cause);
    }

    public FileServiceUnwritableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceUnwritableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
