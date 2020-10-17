package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathNotWritable extends PathException {
    public PathNotWritable(FileData source) {
        super(source);
    }
}
