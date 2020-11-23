package com.example.wirelessdroid.common.service;

import com.example.wirelessdroid.common.model.FileMetaData;
import com.example.wirelessdroid.common.model.VirtualFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {

    FileMetaData root();

    FileMetaData meta(VirtualFile file);

    void remove(VirtualFile file);

    List<FileMetaData> dir(VirtualFile dir);

    FileMetaData move(VirtualFile source, VirtualFile destination);

    FileMetaData copy(VirtualFile source, VirtualFile destination);

    FileMetaData write(VirtualFile source, InputStream stream);

    FileMetaData mkdir(VirtualFile source);

    FileMetaData touch(VirtualFile source);

    InputStream read(VirtualFile source);

    boolean isDirectory(VirtualFile file);

    boolean isFile(VirtualFile file);

    boolean isRoot(VirtualFile file);

    boolean exists(VirtualFile file);

}
