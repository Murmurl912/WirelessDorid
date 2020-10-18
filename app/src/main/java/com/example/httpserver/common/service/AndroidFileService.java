package com.example.httpserver.common.service;

import com.example.httpserver.common.exception.*;
import com.example.httpserver.common.model.FileMetaData;
import com.example.httpserver.common.model.FileContext;
import com.example.httpserver.common.model.FileData;
import com.example.httpserver.common.repository.FileContextRepository;
import com.example.httpserver.common.repository.FileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
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
            return FileMetaData.from("/fs-api/" + context.context, file);
        }).collect(Collectors.toList());
    }

    @Override
    public FileMetaData meta(FileData data) {
        if(!exists(data)) {
            throw new PathNotFound(data);
        }
        try {
            return repository.meta(data.uri, realPath(data));
        } catch (Exception e) {
            rethrow(e, data);
            return null;
        }
    }

    @Override
    public List<FileMetaData> dir(FileData data) {
        if(!exists(data)) {
            throw new PathNotFound(data);
        }
        try {
            return repository.dir(data.uri, realPath(data));
        } catch (Exception e) {
            rethrow(e, data);
            return null;
        }
    }

    @Override
    public void remove(FileData data) {
        if(data.path == null || data.path.isEmpty()) {
            throw new PathIsEmpty(data);
        }
        if(!exists(data)) {
            throw new PathNotFound(data);
        }
        try {
            repository.remove(realPath(data), data.recursive);
        } catch (Exception e) {
            rethrow(e, data);
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
        if(!exists(source)) {
            throw new PathNotFound(source);
        }
        try {
            FileMetaData metaData = repository.move(realPath(source), realPath(destination), destination.override);
            metaData.uri = destination.uri;
            return metaData;
        }   catch (Exception e) {
            rethrow(e, source);
            return null;
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

        if(!exists(source)) {
            throw new PathNotFound(source);
        }

        try {
            FileMetaData metaData = repository.copy(realPath(source), realPath(destination), destination.override);
            metaData.uri = destination.uri;
            return metaData;
        } catch (Exception e) {
            rethrow(e, source);
            return null;
        }
    }

    @Override
    public FileMetaData write(FileData source, InputStream stream) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        try {
            FileMetaData fileMetaData = repository.write(realPath(source), stream, source.override);
            fileMetaData.uri = source.uri;
            return fileMetaData;
        } catch (Exception e) {
            rethrow(e, source);
            return null;
        }
    }

    @Override
    public FileMetaData mkdir(FileData source) {
        if(source.path == null || source.path.isEmpty()) {
            throw new PathIsEmpty(source);
        }
        try {
            FileMetaData metaData = repository.mkdir(realPath(source));
            metaData.uri = source.uri;
            return metaData;
        } catch (Exception e) {
            rethrow(e, source);
            return null;
        }
    }

    @Override
    public FileMetaData touch(FileData context) {
        if(context.path == null || context.path.isEmpty()) {
            throw new PathIsEmpty(context);
        }
        try {
            return repository.touch(realPath(context));
        } catch (Exception e) {
            rethrow(e, context);
            return null;
        }
    }

    @Override
    public InputStream read(FileData source) {
        try {
            return repository.read(realPath(source));
        } catch (Exception e) {
            rethrow(e, source);
            return null;
        }
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

    private void rethrow(Exception e, FileData data) {
        if(e instanceof FileSystemException) {
            rethrow((FileSystemException) e, data);
        } else if(e instanceof IOException) {
            rethrow((IOException)e, data);
        } else {
            throw new RuntimeException(e);
        }
    }

    private void rethrow(FileSystemException e, FileData data) {
        if(e instanceof NoSuchFileException) {
            throw new PathNotFound(data);
        } else if(e instanceof NotDirectoryException) {
            throw new PathIsDirectory(data);
        } else if(e instanceof DirectoryNotEmptyException) {
            throw new PathNotEmpty(data);
        } else if(e instanceof FileAlreadyExistsException) {
            throw new PathExistsException(data);
        } else if(e instanceof AccessDeniedException) {
            throw new PathDeniedException(data);
        } else {
            throw new PathException(data);
        }
    }

    private void rethrow(IOException e, FileData data) {
        if(e instanceof FileNotFoundException) {
            throw new PathNotFound(data);
        } else {
            throw new PathIOException(data);
        }
    }

}
