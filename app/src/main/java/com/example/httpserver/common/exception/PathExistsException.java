package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathExistsException extends PathException {
    public PathExistsException(FileData source) {
        super(source);
    }
}
