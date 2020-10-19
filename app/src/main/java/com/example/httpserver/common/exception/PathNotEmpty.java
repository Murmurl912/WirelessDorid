package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathNotEmpty extends PathException {
    public PathNotEmpty(VirtualFile source) {
        super(source);
    }
}
