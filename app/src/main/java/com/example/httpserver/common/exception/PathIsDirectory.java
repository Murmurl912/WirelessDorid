package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathIsDirectory extends PathException {
    public PathIsDirectory(FileData source) {
        super(source);
    }
}
