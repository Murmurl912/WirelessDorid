package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathIsFile extends PathException {
    public PathIsFile(FileData source) {
        super(source);
    }
}
