package com.example.httpserver.service;


import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface FileService {

    public Path rename(Path path, String name, String proxy);

    public Path move(Path path, Path destination, String proxy);

    public void remove(Path path, String proxy);

    public Path copy(Path path, Path destination, String proxy);

    public Path mkdir(Path path);

    public Path touch(Path path);

    public Path create(Path path, InputStream stream, String proxy);

    public void read(Path path, OutputStream stream);

    public Map<String, String> meta(Path path);

    public Set<Path> dir(Path path);

}