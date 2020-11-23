package com.example.wirelessdroid.common.exception;

import com.example.wirelessdroid.common.model.VirtualFile;

public class PathNotEnabled extends PathException {
    public PathNotEnabled(VirtualFile source) {
        super(source);
    }
}
