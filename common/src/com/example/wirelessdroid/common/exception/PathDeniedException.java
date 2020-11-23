package com.example.wirelessdroid.common.exception;


import com.example.wirelessdroid.common.model.VirtualFile;

public class PathDeniedException extends PathException {
    public PathDeniedException(VirtualFile source) {
        super(source);
    }

    public PathDeniedException(VirtualFile source, VirtualFile destination) {
        super(source, destination);
    }
}
