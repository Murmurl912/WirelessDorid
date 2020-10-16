package com.example.httpserver.service;

import java.nio.file.Path;

public class FileServiceException extends RuntimeException {

    public static final int CODE_PATH_EXISTS = 101;
    public static final int CODE_IO_EXCEPTION = 102;
    public static final int PATH_NOT_EXISTS = 103;
    public static final int CODE_NOT_EMPTY = 104;
    public static final int CODE_UNREADABLE = 105;
    public static final int CODE_UNWRITABLE = 106;
    public static final int CODE_IS_FILE = 107;
    public static final int CODE_IS_DIRECTORY = 108;
    private int code = 101;
    private Path path;

    public FileServiceException(int code, Path path) {
        this.code = code;
        this.path = path;
    }

    public FileServiceException(String message, int code, Path path) {
        super(message);
        this.code = code;
        this.path = path;
    }

    public FileServiceException(String message, Throwable cause, int code, Path path) {
        super(message, cause);
        this.code = code;
        this.path = path;
    }

    public FileServiceException(Throwable cause, int code, Path path) {
        super(cause);
        this.code = code;
        this.path = path;
    }

    public FileServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code, Path path) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
