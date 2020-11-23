package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathIsFile extends PathException {
    public PathIsFile(VirtualFile source) {
        super(source);
    }
}
