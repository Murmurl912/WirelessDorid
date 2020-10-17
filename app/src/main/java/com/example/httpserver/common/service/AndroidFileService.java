package com.example.httpserver.common.service;

import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.common.model.FileContext;
import com.example.httpserver.common.model.FileData;
import com.example.httpserver.common.repository.FileContextRepository;
import com.example.httpserver.common.repository.FileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AndroidFileService implements FileService {

    private final FileRepository repository;
    private final FileContextRepository contextRepository;

    public AndroidFileService(FileRepository repository,
                              FileContextRepository contextRepository) {
        this.repository = repository;
        this.contextRepository = contextRepository;
    }

    @Override
    public List<FileMetaData> root() {
        return contextRepository.all().stream().map(context -> {
            File file = new File(context.path);
            return FileMetaData.from("/" + context.context, file);
        }).collect(Collectors.toList());
    }

    @Override
    public FileMetaData meta(FileData data) throws IOException {
        return repository.meta(data.uri, realPath(data));
    }

    @Override
    public List<FileMetaData> dir(FileData data) throws IOException {
        return repository.dir(data.uri, realPath(data));
    }

    @Override
    public void remove(FileData data) throws IOException {
        if(data.path == null || data.path.isEmpty()) {
            throw new SecurityException();
        }

        repository.remove(realPath(data), data.recursive);
    }

    @Override
    public FileMetaData move(FileData source, FileData destination) throws IOException {
        if(source.path == null || source.path.isEmpty()) {
            throw new SecurityException();
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new SecurityException();
        }

        FileMetaData metaData = repository.move(realPath(source), realPath(destination), destination.override);
        metaData.uri = destination.uri;
        return metaData;
    }

    @Override
    public FileMetaData copy(FileData source, FileData destination) throws IOException {
        if(source.path == null || source.path.isEmpty()) {
            throw new SecurityException();
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new SecurityException();
        }

        FileMetaData metaData = repository.copy(realPath(source), realPath(destination), destination.override);
        metaData.uri = destination.uri;
        return metaData;
    }

    @Override
    public FileMetaData write(FileData source, InputStream stream) throws IOException {
        if(source.path == null || source.path.isEmpty()) {
            throw new SecurityException();
        }
        FileMetaData fileMetaData = repository.write(realPath(source), stream, source.override);
        fileMetaData.uri = source.uri;
        return fileMetaData;
    }

    @Override
    public FileMetaData mkdir(FileData source) throws IOException {
        if(source.path == null || source.path.isEmpty()) {
            throw new SecurityException();
        }
        FileMetaData metaData = repository.mkdir(realPath(source));
        metaData.uri = source.uri;
        return metaData;
    }

    @Override
    public FileMetaData touch(FileData context) {
        return null;
    }

    @Override
    public InputStream read(FileData source) throws IOException {
        return repository.read(realPath(source));
    }

    @Override
    public boolean isDirectory(FileData data) {
        FileContext context = contextRepository.find(data.context);
        if(context == null) {
            return false;
        }
        Path path = Paths.get(context.path, data.path);
        return Files.isDirectory(path);
    }

    @Override
    public boolean isFile(FileData data) {
        return !isDirectory(data);
    }

    @Override
    public boolean isRoot(FileData data) {
        return data != null &&(data.path == null || data.path.isEmpty()) ;
    }

    @Override
    public boolean exists(FileData data) {
        FileContext context = contextRepository.find(data.context);
        if(context == null) {
            return false;
        }
        Path path = Paths.get(context.path, data.path);
        return Files.exists(path);
    }

    private Path realPath(FileData data) throws IOException {
        FileContext context = contextRepository.find(data.context);
        if(context == null) {
            throw new FileNotFoundException();
        }
        return Paths.get(context.path, data.path);
    }
}
