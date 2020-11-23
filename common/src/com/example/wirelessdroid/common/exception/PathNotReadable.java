package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathNotReadable extends PathException {
    public PathNotReadable(VirtualFile source) {
        super(source);
    }
}
