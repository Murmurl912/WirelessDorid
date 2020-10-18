package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathNotReadable extends PathException {
    public PathNotReadable(FileData source) {
        super(source);
    }
}