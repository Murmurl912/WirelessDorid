package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathNotEnabled extends PathException {
    public PathNotEnabled(FileData source) {
        super(source);
    }
}
