package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathContextNotFound extends PathException {
    public PathContextNotFound(VirtualFile source) {
        super(source);
    }
}