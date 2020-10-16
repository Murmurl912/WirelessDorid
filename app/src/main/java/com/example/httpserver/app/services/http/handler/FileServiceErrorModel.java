package com.example.httpserver.app.services.http.handler;

import com.example.httpserver.service.*;
import fi.iki.elonen.NanoHTTPD;

import java.nio.file.Path;
import java.util.Date;

public class FileServiceErrorModel {
    public int status;
    public int code;
    public Path path;
    public String uri;
    public String message;
    public Date timestamp;
    public Exception error;

    public static FileServiceErrorModel of(String uri, FileServiceException e) {
        FileServiceErrorModel model = new FileServiceErrorModel();
        model.code = e.getCode();
        model.path = e.getPath();
        model.uri = uri;
        model.error = e;
        model.timestamp = new Date();
        if(e instanceof FileServiceExistsException) {
            model.status = NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus();
            model.message = "a path has already exists";
        } else if (e instanceof FileServiceIsDirectoryException) {
            model.status = NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus();
            model.message = "path is a directory";
        } else if(e instanceof FileServiceIsFileException) {
            model.status = NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus();
            model.message = "path is a file";
        } else if(e instanceof FileServiceNotEmptyException) {
            model.status = NanoHTTPD.Response.Status.BAD_REQUEST.getRequestStatus();
            model.message = "path is not empty";
        } else if(e instanceof FileServiceNotFoundException) {
            model.status = NanoHTTPD.Response.Status.NOT_FOUND.getRequestStatus();
            model.message = "path is not found or cannot be accessed";
        } else if(e instanceof FileServiceUnreadableException) {
            model.status = NanoHTTPD.Response.Status.INTERNAL_ERROR.getRequestStatus();
            model.message = "path cannot be read";
        } else if(e instanceof FileServiceUnwritableException) {
            model.status = NanoHTTPD.Response.Status.INTERNAL_ERROR.getRequestStatus();
            model.message = "path cannot be write";
        } else {
            model.status = NanoHTTPD.Response.Status.INTERNAL_ERROR.getRequestStatus();
            model.message = "io error may occurs during the operation";
        }
        return model;
    }


}
