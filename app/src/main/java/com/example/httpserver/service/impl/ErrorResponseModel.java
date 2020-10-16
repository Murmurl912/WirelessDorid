package com.example.httpserver.service.impl;

import java.nio.file.Path;
import java.util.Date;

public class ErrorResponseModel {
    public int code; // error code
    public int status; // status
    public Date timestamp; // timestamp
    public String method;
    public String uri; // uri
    public String message; // message
    public Exception error; // error

    @Override
    public String toString() {
        return "{" +
                "\"code=\"" + code +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", method=\"" + method + "\"" +
                ", \"uri\"=\"" + uri + '\"' +
                ", message=\"" + message + '\"' +
                ", error=\"" + error.toString() + "\"" +
                '}';
    }

    public static ErrorResponseModel unauthorized(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 1;
        model.status = 401;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        return model;
    }

    public static ErrorResponseModel forbidden(String method, String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 2;
        model.status = 403;
        model.timestamp = new Date();
        model.uri = uri;
        model.method = method;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel notfound(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 3;
        model.status = 404;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel exists(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 4;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel isfile(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 5;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel isdir(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 6;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel notempty(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 7;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel unreadable(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 8;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel unwritable(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 9;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel isroot(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 10;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel bad(String uri) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 10;
        model.status = 400;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = null;
        model.error = null;
        return model;
    }

    public static ErrorResponseModel error(String uri, Exception e) {
        ErrorResponseModel model = new ErrorResponseModel();
        model.code = 10;
        model.status = 500;
        model.timestamp = new Date();
        model.uri = uri;
        model.message = e.getMessage();
        model.error = e;
        return model;
    }

}
