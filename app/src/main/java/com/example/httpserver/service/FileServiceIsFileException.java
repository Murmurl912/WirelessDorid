package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceIsFileException extends FileServiceException {
    public int code = 401;
    public FileServiceIsFileException() {
        super();
    }

    public FileServiceIsFileException(Path path) {
        super(path);
    }

    public FileServiceIsFileException(String message, Path path) {
        super(message, path);
    }

    public FileServiceIsFileException(String message) {
        super(message);
    }

    public FileServiceIsFileException(String message, Throwable cause, Path path) {
        super(message, cause, path);
    }

    public FileServiceIsFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceIsFileException(Throwable cause, Path path) {
        super(cause, path);
    }

    public FileServiceIsFileException(Throwable cause) {
        super(cause);
    }

    public FileServiceIsFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, path);
    }

    protected FileServiceIsFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
