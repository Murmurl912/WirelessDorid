package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathContextNotFound extends PathException {
    public PathContextNotFound(FileData source) {
        super(source);
    }
}