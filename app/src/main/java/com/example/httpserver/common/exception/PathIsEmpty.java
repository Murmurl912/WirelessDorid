package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathIsEmpty extends PathException {
    public PathIsEmpty(FileData source) {
        super(source);
    }
}
