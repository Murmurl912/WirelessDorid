package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceExistsException extends FileServiceException {
    public FileServiceExistsException(int code, Path path) {
        super(code, path);
    }

    public FileServiceExistsException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceExistsException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceExistsException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
