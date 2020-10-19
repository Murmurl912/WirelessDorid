package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathNotFound extends PathException {
    public PathNotFound(VirtualFile source) {
        super(source);
    }
}
