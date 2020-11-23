package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathNotFound extends PathException {
    public PathNotFound(VirtualFile source) {
        super(source);
    }
}
