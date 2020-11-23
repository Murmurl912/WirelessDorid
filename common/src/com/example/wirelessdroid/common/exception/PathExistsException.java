package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathExistsException extends PathException {
    public PathExistsException(VirtualFile source) {
        super(source);
    }
}
