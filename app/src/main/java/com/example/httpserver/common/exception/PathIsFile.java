package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathIsFile extends PathException {
    public PathIsFile(VirtualFile source) {
        super(source);
    }
}
