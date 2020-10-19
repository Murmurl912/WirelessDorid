package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathNotReadable extends PathException {
    public PathNotReadable(VirtualFile source) {
        super(source);
    }
}
