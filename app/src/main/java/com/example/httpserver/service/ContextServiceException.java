package com.example.httpserver.service;

public class ContextServiceException extends RuntimeException {
    public static final int CONDE_CONTEXT_NOT_FOUND = 201;
    public static final int CODE_CONTEXT_FORBIDDEN = 202;

    public int code;
    public String context;
    public String path;

    public ContextServiceException(int code, String context, String path) {
        this.code = code;
        this.context = context;
        this.path = path;
    }

    public ContextServiceException(String message, int code, String context, String path) {
        super(message);
        this.code = code;
        this.context = context;
        this.path = path;
    }

    public ContextServiceException(String message, Throwable cause, int code, String context, String path) {
        super(message, cause);
        this.code = code;
        this.context = context;
        this.path = path;
    }

    public ContextServiceException(Throwable cause, int code, String context, String path) {
        super(cause);
        this.code = code;
        this.context = context;
        this.path = path;
    }

    public ContextServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, String context, String path) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.context = context;
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
