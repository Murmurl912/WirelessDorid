package com.example.httpserver.common.model;

import java.util.Date;

public class ResponseModel {

    public int code; // error code
    public int status; // status
    public Date timestamp; // timestamp
    public String method;
    public String uri; // uri
    public String message; // message
    public Exception error; // error



}
