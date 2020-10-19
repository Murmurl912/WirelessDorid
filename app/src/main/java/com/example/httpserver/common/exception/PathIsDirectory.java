package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathIsDirectory extends PathException {
    public PathIsDirectory(VirtualFile source) {
        super(source);
    }
}
