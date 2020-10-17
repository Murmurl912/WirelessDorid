package com.example.httpserver.common.service;

import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.common.model.FileData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {

    List<FileMetaData> root();

    FileMetaData meta(FileData data) throws IOException;

    void remove(FileData data) throws IOException;

    List<FileMetaData> dir(FileData data) throws IOException;

    FileMetaData move(FileData source, FileData destination) throws IOException;

    FileMetaData copy(FileData source, FileData destination) throws IOException;

    FileMetaData write(FileData source, InputStream stream) throws IOException;

    FileMetaData mkdir(FileData source) throws IOException;

    FileMetaData touch(FileData source);

    InputStream read(FileData source) throws IOException;

    boolean isDirectory(FileData data);

    boolean isFile(FileData data);

    boolean isRoot(FileData data);

    boolean exists(FileData data);

}
