package com.example.httpserver.service;

public class ContextServiceContextNotFoundException extends ContextServiceException {
    public ContextServiceContextNotFoundException(int code, String context, String path) {
        super(code, context, path);
    }

    public ContextServiceContextNotFoundException(String message, int code, String context, String path) {
        super(message, code, context, path);
    }

    public ContextServiceContextNotFoundException(String message, Throwable cause, int code, String context, String path) {
        super(message, cause, code, context, path);
    }

    public ContextServiceContextNotFoundException(Throwable cause, int code, String context, String path) {
        super(cause, code, context, path);
    }

    public ContextServiceContextNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, String context, String path) {
        super(message, cause, enableSuppression, writableStackTrace, code, context, path);
    }
}
