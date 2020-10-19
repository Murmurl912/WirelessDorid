package com.example.httpserver.common.service;

import com.example.httpserver.common.exception.*;
import com.example.httpserver.common.model.FileRootView;
import com.example.httpserver.common.model.VirtualFile;
import com.example.httpserver.common.model.FileMetaData;
import com.example.httpserver.common.repository.FileContextRepository;
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
            return FileMetaData.from("/fs-api/", file);
        }).collect(Collectors.toList());
    }

    @Override
    public FileMetaData meta(VirtualFile file) {
        FileRootView context = context(file);
        Path p = path(file, context);
        if(!context.read) {
            throw new PathNotReadable(file);
        }

        if(!Files.exists(p)) {
            throw new PathNotFound(file);
        }
        return FileMetaData.from(file.uri, p);
    }

    @Override
    public void remove(VirtualFile file) {

        if(file.path == null || file.path.isEmpty()) {
            throw new PathIsEmpty(file);
        }
        FileRootView context = context(file);
        Path p = path(file, context);
        if(!context.read) {
            throw new PathNotReadable(file);
        }
        if(!context.write) {
            throw new PathNotWritable(file);
        }

        if(!Files.exists(p)) {
            throw new PathNotFound(file);
        }

        try {
            if(Files.isDirectory(p) && file.recursive) {
                FileUtils.deleteDirectory(p.toFile());
            } else {
                Files.delete(p);
            }
        } catch (DirectoryNotEmptyException e) {
            throw new PathNotEmpty(file);
        } catch (AccessDeniedException e) {
            throw new PathDeniedException(file);
        } catch (IOException e) {
            throw new PathException(file);
        }
    }

    @Override
    public List<FileMetaData> dir(VirtualFile dir) {
        FileRootView context = context(dir);
        Path p = path(dir, context);
        if(!context.read) {
            throw new PathNotReadable(dir);
        }
        if(!Files.exists(p)) {
            throw new PathNotFound(dir);
        }
        if(!Files.isDirectory(p)) {
            throw new PathIsFile(dir);
        }
        try {
            return FileMetaData.dir(dir.uri, p.toFile());
        } catch (Exception e) {
            throw new PathException(dir);
        }
    }

    @Override
    public FileMetaData move(VirtualFile source, VirtualFile destination) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new PathIsEmpty(destination);
        }
        FileRootView sc = context(source);
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

        FileRootView dc = context(destination);
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
    public FileMetaData copy(VirtualFile source, VirtualFile destination) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        if(destination.path == null || destination.path.isEmpty()) {
            throw new PathIsEmpty(destination);
        }
        FileRootView sc = context(source);
        Path sp = path(source, sc);
        if(!sc.read) {
            throw new PathNotReadable(source);
        }
        if(!Files.exists(sp)) {
            throw new PathNotFound(source);
        }

        FileRootView dc = context(destination);
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
    public FileMetaData write(VirtualFile source, InputStream stream) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileRootView context = context(source);
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
    public FileMetaData mkdir(VirtualFile source) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileRootView context = context(source);
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
    public FileMetaData touch(VirtualFile source) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        FileRootView context = context(source);
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
    public InputStream read(VirtualFile source) {
        FileRootView context = context(source);
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
    public boolean isDirectory(VirtualFile file) {
        return Files.isDirectory(path(file));
    }

    @Override
    public boolean isFile(VirtualFile file) {
        Path path = path(file);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    @Override
    public boolean isRoot(VirtualFile file) {
        return file.path == null || file.path.isEmpty();
    }

    @Override
    public boolean exists(VirtualFile file) {
        return Files.exists(path(file));
    }

    private FileRootView context(VirtualFile data) {
        FileRootView context = repository.find(data.context);
        if(context == null || !context.share) {
            throw new PathContextNotFound(data);
        }
        return context;
    }

    private Path path(VirtualFile data, FileRootView context) {
        return Paths.get(context.path, data.path);
    }

    private Path path(VirtualFile data) {
        FileRootView context = repository.find(data.context);
        if(context == null) {
            throw new PathContextNotFound(data);
        }

        return Paths.get(context.path, data.path);
    }
}
