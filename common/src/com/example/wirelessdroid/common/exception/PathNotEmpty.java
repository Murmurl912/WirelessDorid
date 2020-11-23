package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathNotEmpty extends PathException {
    public PathNotEmpty(VirtualFile source) {
        super(source);
    }
}
