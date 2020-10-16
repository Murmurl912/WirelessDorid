package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceIOException extends FileServiceException {
    public FileServiceIOException(int code, Path path) {
        super(code, path);
    }

    public FileServiceIOException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceIOException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceIOException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
