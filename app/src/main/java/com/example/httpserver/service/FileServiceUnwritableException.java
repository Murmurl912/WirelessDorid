package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceUnwritableException extends FileServiceException {

    public FileServiceUnwritableException(int code, Path path) {
        super(code, path);
    }

    public FileServiceUnwritableException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceUnwritableException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceUnwritableException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceUnwritableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
