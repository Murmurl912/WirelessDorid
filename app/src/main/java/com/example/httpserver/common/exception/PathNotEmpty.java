package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathNotEmpty extends PathException {
    public PathNotEmpty(FileData source) {
        super(source);
    }
}
