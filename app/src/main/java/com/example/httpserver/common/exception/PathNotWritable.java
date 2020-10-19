package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathNotWritable extends PathException {
    public PathNotWritable(VirtualFile source) {
        super(source);
    }
}
