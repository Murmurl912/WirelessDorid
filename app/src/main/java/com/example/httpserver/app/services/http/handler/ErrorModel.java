package com.example.httpserver.app.services.http.handler;

import com.example.httpserver.service.FileServiceException;

import java.nio.file.Path;
import java.util.Date;

public class ErrorModel {
    public int status;
    public int code;
    public Path path;
    public String uri;
    public String message;
    public Date timestamp;
    public Exception error;

    public static ErrorModel of(int code, int status, String uri, String message, Exception e) {
        ErrorModel model = new ErrorModel();
        model.code = code;
        model.status = status;
        model.uri = uri;
        model.error = e;
        model.message = message;
        model.timestamp = new Date();
        return model;
    }
}
