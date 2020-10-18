package com.example.httpserver.common.service;

import com.example.httpserver.common.model.FileMetaData;
import com.example.httpserver.common.model.FileData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {

    List<FileMetaData> root();

    FileMetaData meta(FileData data);

    void remove(FileData data);

    List<FileMetaData> dir(FileData data);

    FileMetaData move(FileData source, FileData destination);

    FileMetaData copy(FileData source, FileData destination);

    FileMetaData write(FileData source, InputStream stream);

    FileMetaData mkdir(FileData source);

    FileMetaData touch(FileData source);

    InputStream read(FileData source);

    boolean isDirectory(FileData data);

    boolean isFile(FileData data);

    boolean isRoot(FileData data);

    boolean exists(FileData data);

}
