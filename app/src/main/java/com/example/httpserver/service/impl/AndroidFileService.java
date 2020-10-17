package com.example.httpserver.service.impl;

import androidx.documentfile.provider.DocumentFile;
import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.service.*;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AndroidFileService implements FileService {

    public static final String PROXY_REPLACE = "replace";
    public static final String PROXY_RECURSIVE = "recursive";
    public static final String PROXY_APPEND = "append";

    @Override
    public Path rename(Path path, String name, String proxy) {
        return move(path, path.resolve(name), proxy);
    }

    /**
     * move path
     * @param path path to be moved
     * @param destination move destination
     * @param proxy proxy for conflicts
     * @return new path of moved file
     * @throws FileServiceNotFoundException when given path cannot be found
     * @throws FileServiceUnreadableException when given path cannot be read
     * @throws FileServiceUnwritableException when given destination's parent path cannot be write
     * @throws FileServiceNotEmptyException when given destination exits and it's a directory, the given path is
     * a file and replace proxy is supplied. an non empty dir cannot be replaced by a file.
     * @throws FileServiceExistsException when destination exists and no replace proxy is supplied
     * @throws FileServiceException when operation failed because of io
     */
    @Override
    public Path move(Path path, Path destination, String proxy) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.writable(destination.getParent());

        if(Files.exists(destination)) {
            Condition.writable(destination);
            if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {

                if(!Files.isDirectory(path) && Files.isDirectory(destination)) {
                    Condition.empty(destination); // conflict dir
                }

                try {
                    Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceIOException(e, FileServiceException.CODE_PATH_EXISTS, path);
                }
            } else {
                throw new FileServiceExistsException(FileServiceException.CODE_PATH_EXISTS, destination);
            }
        } else {
            try {
                if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {
                    Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(path, destination);
                }
            } catch (IOException e) {
                throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
            }
        }
        return destination;
    }

    /**
     * remove given path
     * @param path path to be removed
     * @param proxy remove proxy can be "recursive"
     * @throws FileServiceNotFoundException when given path cannot be found
     * @throws FileServiceUnwritableException when given path cannot be written
     * @throws FileServiceUnreadableException when given path cannot be read
     * @throws FileServiceNotEmptyException when given path is dir, recursive delete proxy is not supplied and dir is
     * not empty
     * @throws FileServiceException when io errors occurs
     */
    @Override
    public void remove(Path path, String proxy) {
        Condition.exist(path);
        Condition.writable(path);
        Condition.readable(path);

        if(Files.isDirectory(path)) {
            try {
                long count = Files.list(path).count();
                if(count > 0) {
                    if(PROXY_RECURSIVE.equalsIgnoreCase(proxy)) {
                        FileUtils.deleteDirectory(path.toFile());
                    } else {
                        throw new FileServiceNotEmptyException(FileServiceException.CODE_NOT_EMPTY, path);
                    }
                }
            } catch (IOException e) {
                throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
            }
        } else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
            }
        }

    }

    /**
     * copy file from given path to given destination
     * @param path path to be copied
     * @param destination destination path when copied
     * @param proxy conflict proxy
     * @return new path of copied path, which must equals to destination
     * @throws FileServiceNotFoundException given source path cannot be found
     * @throws FileServiceUnreadableException given source path cannot be read
     * @throws FileServiceUnwritableException given destination's parent path cannot be write
     * or destination cannot be written
     * @throws FileServiceNotEmptyException given destination is a directory and cannot be replaced
     * @throws FileServiceExistsException given destination existed and is replace proxy not supplied
     * @throws FileServiceException io error occurs when copy file
     */
    @Override
    public Path copy(Path path, Path destination, String proxy) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.writable(destination.getParent());

        if(Files.exists(destination)) {
            Condition.writable(destination);
            if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {

                if(!Files.isDirectory(path) && Files.isDirectory(destination)) {
                    Condition.empty(destination); // conflict dir
                }

                try {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
                }
            } else {
                throw new FileServiceExistsException(FileServiceException.CODE_PATH_EXISTS, path);
            }
        } else {
            try {
                if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(path, destination);
                }
            } catch (IOException e) {
                throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
            }
        }
        return destination;
    }

    /**
     * create an empty file with given path
     * @param path file path
     * @return path of created file
     * @throws FileServiceException if io errors occurs
     * @throws FileServiceUnwritableException if parent path cannot be write
     * @throws FileServiceExistsException if path already exists
     */
    @Override
    public Path touch(Path path) {
        Condition.writable(path.getParent());
        Condition.unexist(path);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new FileServiceIOException(e, FileServiceException.CODE_PATH_EXISTS, path);
        }
        return path;
    }

    /**
     * create a new dir
     * @param path dir path
     * @return path of created dir
     * @throws FileServiceUnwritableException parent path are not writable
     * @throws FileServiceExistsException path already exists
     * @throws FileServiceException io errors occurs
     */
    @Override
    public Path mkdir(Path path) {
        Condition.writable(path.getParent());
        Condition.unexist(path);
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
        }
        return path;
    }

    /**
     * create or override file path with given input stream
     * @param path path to be written
     * @param stream data
     * @param proxy conflicts proxy
     * @return created path
     * @throws FileServiceUnwritableException parent path or path itself cannot be written
     * @throws FileServiceException if io error occurs
     * @throws FileServiceExistsException if path already exits and replace proxy is not supplied
     */
    @Override
    public Path write(Path path, InputStream stream, String proxy) {
        Condition.writable(path.getParent());
        if(Files.exists(path)) {
            Condition.writable(path);
            if(PROXY_REPLACE.equals(proxy)) {
                if(Files.isDirectory(path)) {
                    Condition.empty(path);
                }

                try {
                    Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
                }
            } else {
                throw new FileServiceExistsException(FileServiceException.CODE_PATH_EXISTS, path);
            }
        } else {
            if(PROXY_REPLACE.equals(proxy)) {
                try {
                    Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
                }
            } else {
                try {
                    Files.copy(stream, path);
                } catch (IOException e) {
                    throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
                }
            }
        }
        return path;
    }

    /**
     * write a file path to a output stream
     * @param path path to be read
     * @param stream output stream
     * @throws FileServiceUnreadableException cannot read path
     * @throws FileServiceIsDirectoryException path is not a file
     * @throws FileServiceNotFoundException path is not found
     * @throws FileServiceException io errors
     */
    @Override
    public void read(Path path, OutputStream stream) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.file(path);
        try {
            Files.copy(path, stream);
        } catch (IOException e) {
            throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
        }
    }

    /**
     * read a path to a input stream
     * @param path  path to be read
     * @return input stream
     * @throws FileServiceNotFoundException path not found
     * @throws FileServiceUnreadableException path unreadable
     * @throws FileServiceIsDirectoryException path is a directory
     * @throws FileServiceNotFoundException io errors occurs
     */
    @Override
    public InputStream read(Path path) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.file(path);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new FileServiceIOException(e, FileServiceException.CODE_IO_EXCEPTION, path);
        }
    }

    /**
     * retrieve meta data
     * @param path path
     * @return map of meta data
     * @throws FileServiceNotEmptyException not found
     * @throws FileServiceUnreadableException cannot be read
     * @throws FileServiceException io error
     */
    @Override
    public FileMetaData meta(String uri, Path path) {
        Condition.exist(path);
        Condition.readable(path);

        return FileMetaData.from(uri, path.toFile());
    }

    /**
     * get files under a directory path
     * @param path directory
     * @return list of path
     * @throws FileServiceExistsException path is not found
     * @throws FileServiceUnreadableException path unreadable
     * @throws FileServiceIsFileException path is a file;
     * @throws FileServiceException io errors
     */
    @Override
    public List<FileMetaData> dir(String uri, Path path) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.directory(path);

        return FileMetaData.dir(uri, path.toFile());
    }

    public static class Condition {

        public static void readable(Path path) {
            if(!Files.isReadable(path)) {
                throw new FileServiceUnreadableException(FileServiceException.CODE_UNREADABLE, path);
            }
        }

        public static void writable(Path path) {
            if(!Files.isWritable(path)) {
                throw new FileServiceUnwritableException(FileServiceException.CODE_UNWRITABLE, path);
            }
        }

        public static void exist(Path path) {
            if(!Files.exists(path)) {
                throw new FileServiceNotFoundException(FileServiceException.CODE_NOT_EMPTY, path);
            }
        }

        public static void unexist(Path path) {
            if(Files.exists(path)) {
                throw new FileServiceExistsException(FileServiceException.CODE_PATH_EXISTS, path);
            }
        }

        public static void empty(Path path) {
            try {
                long count = Files.list(path).count();
                if(count > 0) {
                    throw new FileServiceNotEmptyException(FileServiceException.CODE_NOT_EMPTY, path);
                }
            } catch (IOException e) {
                throw new FileServiceException(e, FileServiceException.CODE_IO_EXCEPTION, path);
            }
        }

        public static void directory(Path path) {
            if(!Files.isDirectory(path)) {
                throw new FileServiceIsFileException(FileServiceException.CODE_IS_FILE, path);
            }
        }

        public static void file(Path path) {
            if(Files.isDirectory(path)) {
                throw new FileServiceIsDirectoryException(FileServiceException.CODE_IS_DIRECTORY, path);
            }
        }
    }


}
