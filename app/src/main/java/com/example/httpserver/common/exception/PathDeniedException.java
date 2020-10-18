package com.example.httpserver.common.exception;


import com.example.httpserver.common.model.FileData;

public class PathDeniedException extends PathException {
    public PathDeniedException(FileData source) {
        super(source);
    }

    public PathDeniedException(FileData source, FileData destination) {
        super(source, destination);
    }
}
