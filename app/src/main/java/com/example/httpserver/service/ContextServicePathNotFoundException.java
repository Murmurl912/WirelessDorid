package com.example.httpserver.service;

public class ContextServicePathNotFoundException extends ContextServiceException {
    public ContextServicePathNotFoundException(int code, String context, String path) {
        super(code, context, path);
    }

    public ContextServicePathNotFoundException(String message, int code, String context, String path) {
        super(message, code, context, path);
    }

    public ContextServicePathNotFoundException(String message, Throwable cause, int code, String context, String path) {
        super(message, cause, code, context, path);
    }

    public ContextServicePathNotFoundException(Throwable cause, int code, String context, String path) {
        super(cause, code, context, path);
    }

    public ContextServicePathNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, String context, String path) {
        super(message, cause, enableSuppression, writableStackTrace, code, context, path);
    }
}
