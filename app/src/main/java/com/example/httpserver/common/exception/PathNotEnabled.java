package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathNotEnabled extends PathException {
    public PathNotEnabled(VirtualFile source) {
        super(source);
    }
}
