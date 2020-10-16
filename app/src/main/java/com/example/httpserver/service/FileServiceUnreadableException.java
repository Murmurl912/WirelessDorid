package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceUnreadableException extends FileServiceException {
    public FileServiceUnreadableException(int code, Path path) {
        super(code, path);
    }

    public FileServiceUnreadableException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceUnreadableException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceUnreadableException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceUnreadableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
