package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceIsFileException extends FileServiceException {
    public FileServiceIsFileException(int code, Path path) {
        super(code, path);
    }

    public FileServiceIsFileException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceIsFileException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceIsFileException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceIsFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
