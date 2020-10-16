package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceNotEmptyException extends FileServiceException {
    public FileServiceNotEmptyException(int code, Path path) {
        super(code, path);
    }

    public FileServiceNotEmptyException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceNotEmptyException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceNotEmptyException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceNotEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
