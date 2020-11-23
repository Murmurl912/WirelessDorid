package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathIsDirectory extends PathException {
    public PathIsDirectory(VirtualFile source) {
        super(source);
    }
}
