package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathIOException extends PathException {
    public PathIOException(VirtualFile source) {
        super(source);
    }
}
