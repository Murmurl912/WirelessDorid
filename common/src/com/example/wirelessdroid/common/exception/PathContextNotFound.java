package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathContextNotFound extends PathException {
    public PathContextNotFound(VirtualFile source) {
        super(source);
    }
}