package com.example.httpserver.app.services.http;

import java.util.Date;

public class ErrorResponseModel {
    public int code; // error code
    public int status; // status
    public Date timestamp; // timestamp
    public String uri; // uri
    public String message; // message
    public Exception error; // error
}
