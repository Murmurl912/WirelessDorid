package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathExistsException extends PathException {
    public PathExistsException(VirtualFile source) {
        super(source);
    }
}
