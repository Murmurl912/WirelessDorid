package com.example.httpserver.common.repository;

import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.service.*;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.List;

public interface FileRepository {

    default public FileMetaData move(Path path, Path destination, boolean override)
            throws SecurityException, IOException {
        if(override)
            Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
        else
            Files.move(path, destination);
        return FileMetaData.from(null, path);
    }

    default public void remove(Path path, boolean recursive)
            throws IOException, SecurityException {
        if(!Files.exists(path)) {
            throw new FileNotFoundException(path.toFile().getPath());
        }

        if(Files.isDirectory(path) && recursive) {
            FileUtils.deleteDirectory(path.toFile());
        } else {
            Files.delete(path);
        }
    };

    default public FileMetaData copy(Path path, Path destination, boolean override)
            throws IOException, SecurityException{
        if(override) {
            Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(path, destination);
        }
        return FileMetaData.from(null, path);
    }

    default public FileMetaData mkdir(Path path) throws IOException, SecurityException {
        Files.createDirectory(path);
        return FileMetaData.from(null, path);
    }

    default public FileMetaData touch(Path path) throws IOException, SecurityException {
        Files.createFile(path);
        return FileMetaData.from(null, path);
    }


    default public FileMetaData write(Path path, InputStream stream, boolean override)
            throws IOException, SecurityException {

        if(override) {
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(stream, path);
        }
        return FileMetaData.from(null, path);
    }

    default FileMetaData read(Path path, OutputStream stream)
            throws IOException, SecurityException {
        Files.copy(path, stream);
        return FileMetaData.from(null, path);
    }

    default InputStream read(Path path) throws IOException {
        return Files.newInputStream(path);
    }

    default FileMetaData meta(String uri, Path path) throws FileNotFoundException {
        if(!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
        return FileMetaData.from(uri, path.toFile());
    }

    public default List<FileMetaData> dir(String uri, Path path) throws FileNotFoundException {
        if(!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
        return FileMetaData.dir(uri, path.toFile());
    }

}
