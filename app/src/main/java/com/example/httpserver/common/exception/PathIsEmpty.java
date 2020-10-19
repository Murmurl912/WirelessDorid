package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathIsEmpty extends PathException {
    public PathIsEmpty(VirtualFile source) {
        super(source);
    }
}
