package com.example.httpserver.common.exception;

import com.example.httpserver.common.model.FileData;

public class PathException extends ServiceException {
    private FileData source;
    private FileData destination;

    public PathException(FileData source) {
        this.source = source;
    }

    public PathException(FileData source, FileData destination) {
        this.source = source;
        this.destination = destination;
    }

    public FileData getDestination() {
        return destination;
    }

    public FileData getSource() {
        return source;
    }
}
