package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceIsDirectoryException extends FileServiceException {
    public FileServiceIsDirectoryException(int code, Path path) {
        super(code, path);
    }

    public FileServiceIsDirectoryException(String message, int code, Path path) {
        super(message, code, path);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause, int code, Path path) {
        super(message, cause, code, path);
    }

    public FileServiceIsDirectoryException(Throwable cause, int code, Path path) {
        super(cause, code, path);
    }

    public FileServiceIsDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace, code, path);
    }
}
