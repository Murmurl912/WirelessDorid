package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathIOException extends PathException {
    public PathIOException(VirtualFile source) {
        super(source);
    }
}
