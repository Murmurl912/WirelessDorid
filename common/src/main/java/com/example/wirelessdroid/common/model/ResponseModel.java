package com.example.wirelessdroid.common.model;

import java.util.Date;

public class ResponseModel {

    public int code; // error code
    public int status; // status
    public Date timestamp; // timestamp
    public String method;
    public String uri; // uri
    public String message; // message
    public Exception error; // error

    public static void notfound(String uri) {

    }

    ;

    public static void forbidden(String uri) {

    }

    public static void exits(String uri) {

    }

    public static void notempty(String uri) {

    }

    public static void invalid(String uri) {

    }

    public static void isempty(String uri) {

    }

    public static void isfile(String uri) {

    }

    public static void notpublic() {
    }

}
