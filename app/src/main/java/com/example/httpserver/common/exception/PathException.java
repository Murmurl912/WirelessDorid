package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.VirtualFile;

public class PathException extends ServiceException {
    private VirtualFile source;
    private VirtualFile destination;

    public PathException(VirtualFile source) {
        this.source = source;
    }

    public PathException(VirtualFile source, VirtualFile destination) {
        this.source = source;
        this.destination = destination;
    }

    public VirtualFile getDestination() {
        return destination;
    }

    public VirtualFile getSource() {
        return source;
    }
}
