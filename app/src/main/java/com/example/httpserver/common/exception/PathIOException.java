package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathIOException extends PathException {
    public PathIOException(FileData source) {
        super(source);
    }
}
