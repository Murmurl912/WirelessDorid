package com.example.httpserver.service;

public class FileServiceIsFileException extends FileServiceException {
    public FileServiceIsFileException() {
        super();
    }

    public FileServiceIsFileException(String message) {
        super(message);
    }

    public FileServiceIsFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceIsFileException(Throwable cause) {
        super(cause);
    }

    protected FileServiceIsFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
