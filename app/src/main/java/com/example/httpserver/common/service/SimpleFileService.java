package com.example.httpserver.common.service;

import com.example.httpserver.common.exception.*;
import com.example.httpserver.common.model.FileContext;
import com.example.httpserver.common.model.FileData;
import com.example.httpserver.common.model.FileMetaData;
import com.example.httpserver.common.repository.FileContextRepository;
import com.example.httpserver.common.repository.FileRepository;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleFileService implements FileService {

    private final FileContextRepository repository;

    public SimpleFileService(FileContextRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FileMetaData> root() {
        return repository.all().stream().map(context -> {
            File file = new File(context.path);
            return FileMetaData.from("/fs-api/" + context.context, file);
        }).collect(Collectors.toList());
    }

    @Override
    public FileMetaData meta(FileData data) {
        FileContext context = context(data);
        Path p = path(data, context);
        if(!context.read) {
            throw new PathNotReadable(data);
        }

        if(!Files.exists(p)) {
            throw new PathNotFound(data);
        }
        return FileMetaData.from(data.uri, p);
    }

    @Override
    public void remove(FileData data) {

        if(data.path == null || data.path.isEmpty()) {
            throw new PathIsEmpty(data);
        }
        FileContext context = context(data);
        Path p = path(data, context);
        if(!context.read) {
            throw new PathNotReadable(data);
        }
        if(!context.write) {
            throw new PathNotWritable(data);
        }

        if(!Files.exists(p)) {
            throw new PathNotFound(data);
        }

        try {
            if(Files.isDirectory(p) && data.recursive) {
                FileUtils.deleteDirectory(p.toFile());
            } else {
                Files.delete(p);
            }
        } catch (DirectoryNotEmptyException e) {
            throw new PathNotEmpty(data);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(data);
        } catch (IOException e) {
            throw new PathException(data);
        }
    }

    @Override
    public List<FileMetaData> dir(FileData data) {
        FileContext context = context(data);
        Path p = path(data, context);
        if(!context.read) {
            throw new PathNotReadable(data);
        }
        if(!Files.exists(p)) {
            throw new PathNotFound(data);
        }
        if(!Files.isDirectory(p)) {
            throw new PathIsFile(data);
        }
        try {
            return FileMetaData.dir(data.uri, p.toFile());
        } catch (Exception e) {
            throw new PathException(data);
        }
    }

    @Override
    public FileMetaData move(FileData source, FileData destination) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new PathIsEmpty(destination);
        }
        FileContext sc = context(source);
        Path sp = path(source, sc);
        if(!sc.read) {
            throw new PathNotReadable(source);
        }
        if(!sc.write) {
            throw new PathNotWritable(source);
        }
        if(!Files.exists(sp)) {
            throw new PathNotFound(source);
        }

        FileContext dc = context(destination);
        Path dp = path(destination, dc);
        if(!dc.read) {
            throw new PathNotReadable(destination);
        }
        if(!dc.write) {
            throw new PathNotWritable(destination);
        }
        if(!Files.exists(sp)) {
            throw new PathNotFound(source);
        }        try {
            if(destination.override) {
                Files.move(sp, dp, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(sp, dp);
            }
            return FileMetaData.from(destination.uri, dp);
        } catch (NoSuchFileException e) {
            throw new PathNotFound(destination);
        } catch (DirectoryNotEmptyException e) {
            throw new PathNotEmpty(destination);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source, destination);
        } catch (IOException e) {
            throw new PathException(source, destination);
        }
    }

    @Override
    public FileMetaData copy(FileData source, FileData destination) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new PathIsEmpty(destination);
        }
        FileContext sc = context(source);
        Path sp = path(source, sc);
        if(!sc.read) {
            throw new PathNotReadable(source);
        }
        if(!Files.exists(sp)) {
            throw new PathNotFound(source);
        }

        FileContext dc = context(destination);
        Path dp = path(destination, dc);
        if(!dc.read) {
            throw new PathNotReadable(destination);
        }
        if(!dc.write) {
            throw new PathNotWritable(destination);
        }
        if(!Files.exists(sp)) {
            throw new PathNotFound(source);
        }        try {
            if(destination.override) {
                Files.copy(sp, dp, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(sp, dp);
            }
            return FileMetaData.from(destination.uri, dp);
        } catch (NoSuchFileException e) {
            throw new PathNotFound(destination);
        } catch (DirectoryNotEmptyException e) {
            throw new PathNotEmpty(destination);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source, destination);
        } catch (IOException e) {
            throw new PathException(source, destination);
        }
    }

    @Override
    public FileMetaData write(FileData source, InputStream stream) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileContext context = context(source);
        Path p = path(source, context);
        if(!context.read) {
            throw new PathNotReadable(source);
        }
        if(!context.write) {
            throw new PathNotWritable(source);
        }
        try {
            if(source.override) {
                Files.copy(stream, p, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(stream, p);
            }
            return FileMetaData.from(source.uri, p);
        } catch (NoSuchFileException e) {
            throw new PathNotFound(source);
        } catch (FileAlreadyExistsException e) {
            throw new PathExistsException(source);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source);
        } catch (DirectoryNotEmptyException e) {
            throw new PathNotEmpty(source);
        } catch (IOException e) {
            throw new PathException(source);
        }
    }

    @Override
    public FileMetaData mkdir(FileData source) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileContext context = context(source);
        Path p = path(source, context);
        if(!context.read) {
            throw new PathNotReadable(source);
        }
        if(!context.write) {
            throw new PathNotWritable(source);
        }
        try {
            Files.createDirectory(p);
            return FileMetaData.from(source.uri, p);
        } catch (NoSuchFileException e) {
            throw new PathNotFound(source);
        } catch (FileAlreadyExistsException e) {
            throw new PathExistsException(source);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source);
        } catch (IOException e) {
            throw new PathException(source);
        }
    }

    @Override
    public FileMetaData touch(FileData source) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileContext context = context(source);
        Path p = path(source, context);
        if(!context.read) {
            throw new PathNotReadable(source);
        }
        if(!context.write) {
            throw new PathNotWritable(source);
        }
        try {
            Files.createFile(p);
            return FileMetaData.from(source.uri, p);
        } catch (NoSuchFileException e) {
            throw new PathNotFound(source);
        } catch (FileAlreadyExistsException e) {
            throw new PathExistsException(source);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source);
        } catch (IOException e) {
            throw new PathException(source);
        }
    }

    @Override
    public InputStream read(FileData source) {
        FileContext context = context(source);
        Path p = path(source, context);
        if(!context.read) {
            throw new PathNotReadable(source);
        }

        if(!Files.exists(p)) {
            throw new PathNotFound(source);
        }
        if(Files.isDirectory(p)) {
            throw new PathIsDirectory(source);
        }

        try {
            return Files.newInputStream(p, StandardOpenOption.READ);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(source);
        } catch (FileNotFoundException e) {
            throw new PathNotFound(source);
        } catch (IOException e) {
            throw new PathException(source);
        }
    }

    @Override
    public boolean isDirectory(FileData data) {
        return Files.isDirectory(path(data));
    }

    @Override
    public boolean isFile(FileData data) {
        Path path = path(data);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    @Override
    public boolean isRoot(FileData data) {
        return data.path == null || data.path.isEmpty();
    }

    @Override
    public boolean exists(FileData data) {
        return Files.exists(path(data));
    }

    private FileContext context(FileData data) {
        FileContext context = repository.find(data.context);
        if(context == null || !context.share) {
            throw new PathContextNotFound(data);
        }
        return context;
    }

    private Path path(FileData data, FileContext context) {
        return Paths.get(context.path, data.path);
    }

    private Path path(FileData data) {
        FileContext context = repository.find(data.context);
        if(context == null) {
            throw new PathContextNotFound(data);
        }

        return Paths.get(context.path, data.path);
    }
}
