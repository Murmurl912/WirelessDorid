package com.example.httpserver.common.exception;


import com.example.httpserver.common.model.VirtualFile;

public class PathDeniedException extends PathException {
    public PathDeniedException(VirtualFile source) {
        super(source);
    }

    public PathDeniedException(VirtualFile source, VirtualFile destination) {
        super(source, destination);
    }
}
