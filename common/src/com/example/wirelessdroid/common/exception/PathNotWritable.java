package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathNotWritable extends PathException {
    public PathNotWritable(VirtualFile source) {
        super(source);
    }
}
