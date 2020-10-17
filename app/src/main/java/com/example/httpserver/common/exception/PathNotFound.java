package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathNotFound extends PathException {
    public PathNotFound(FileData source) {
        super(source);
    }
}
