package com.example.httpserver.app.services.http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileMetaData {

    public String uri;
    public String path;
    public String name;
    public boolean directory;
    public boolean writable;
    public boolean readable;
    public boolean executable;
    public long access;
    public long modify;
    public long create;
    public boolean hidden;
    public long size;

    public static FileMetaData from(String uri, File file) {
        FileMetaData metaData = new FileMetaData();
        metaData.uri = uri;
        metaData.path = file.getPath();
        metaData.name = file.getName();
        metaData.directory = file.isDirectory();
        metaData.writable = file.canWrite();
        metaData.readable = file.canRead();
        metaData.executable = file.canExecute();
        metaData.hidden = file.isHidden();
        metaData.access = metaData.create = metaData.modify = file.lastModified();
        metaData.size = file.length();

        return metaData;
    }

    public static FileMetaData from(String uri, Path path) {
        FileMetaData metaData = from(uri, path.toFile());

        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        try {
            BasicFileAttributes attributes = basicFileAttributeView.readAttributes();
            metaData.access = attributes.lastAccessTime().toMillis();
            metaData.modify = attributes.lastModifiedTime().toMillis();
            metaData.create = attributes.creationTime().toMillis();
        } catch (IOException ignored) {

        }

        return metaData;
    }

    public static List<FileMetaData> dir(String uri, File dir) {
        File[] files = dir.listFiles();
        if(files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files).map(file -> {
            String u = uri + "/" + file.getName();
            return from(u, file);
        }).collect(Collectors.toList());
    }
}
