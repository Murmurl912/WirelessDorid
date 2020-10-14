package com.example.httpserver.service;

import androidx.documentfile.provider.DocumentFile;
import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AndroidFileService implements FileService {

    public static final String PROXY_REPLACE = "replace";
    public static final String PROXY_RECURSIVE = "recursive";
    public static final String PROXY_APPEND = "append";

    @Override
    public Path rename(Path path, String name, String proxy) {
        return move(path, path.resolve(name), proxy);
    }

    @Override
    public Path move(Path path, Path destination, String proxy) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.writable(destination);

        if(Files.exists(destination)) {
            if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {

                if(!Files.isDirectory(path) && Files.isDirectory(destination)) {
                    Condition.empty(destination); // conflict dir
                }

                try {
                    Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceException();
                }
            } else {
                throw new FileServiceExistsException();
            }
        } else {
            try {
                if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {
                    Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.move(path, destination);
                }
            } catch (IOException e) {
                throw new FileServiceException();
            }
        }
        return destination;
    }

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
                        throw new FileServiceNotEmptyException();
                    }
                }
            } catch (IOException e) {
                throw new FileServiceException(e);
            }
        } else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new FileServiceException();
            }
        }

    }

    @Override
    public Path copy(Path path, Path destination, String proxy) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.writable(destination);

        if(Files.exists(destination)) {
            if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {

                if(!Files.isDirectory(path) && Files.isDirectory(destination)) {
                    Condition.empty(destination); // conflict dir
                }

                try {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceException();
                }
            } else {
                throw new FileServiceExistsException();
            }
        } else {
            try {
                if(PROXY_REPLACE.equalsIgnoreCase(proxy)) {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(path, destination);
                }
            } catch (IOException e) {
                throw new FileServiceException();
            }
        }
        return destination;
    }

    @Override
    public Path touch(Path path) {
        Condition.writable(path);
        Condition.unexist(path);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
        return path;
    }

    @Override
    public Path mkdir(Path path) {
        Condition.writable(path);
        Condition.unexist(path);
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
        return path;
    }

    @Override
    public Path create(Path path, InputStream stream, String proxy) {
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
                    throw new FileServiceException(e);
                }
            } else {
                throw new FileServiceExistsException();
            }
        } else {
            if(PROXY_REPLACE.equals(proxy)) {
                try {
                    Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new FileServiceException(e);
                }
            } else {
                try {
                    Files.copy(stream, path);
                } catch (IOException e) {
                    throw new FileServiceException(e);
                }
            }
        }
        return path;
    }

    @Override
    public void read(Path path, OutputStream stream) {
        Condition.readable(path);
        Condition.file(path);
        try {
            Files.copy(path, stream);
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
    }

    @Override
    public InputStream read(Path path) {
        Condition.readable(path);
        Condition.file(path);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
    }

    @Override
    public Map<String, String> meta(Path path) {
        Condition.exist(path);
        Condition.readable(path);

        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        try {
            BasicFileAttributes attributes = basicFileAttributeView.readAttributes();
            String name = path.getFileName().toString();
            boolean hidden = Files.isHidden(path);
            boolean readable = Files.isReadable(path);
            boolean writable = Files.isWritable(path);
            boolean directory = Files.isDirectory(path);
            String modifytime = Files.getLastModifiedTime(path).toString();
            String accesstime = attributes.lastAccessTime().toString();
            String creationtime = attributes.creationTime().toString();
            long size = attributes.size();
            boolean regular = attributes.isRegularFile();
            boolean sybmoliclink = attributes.isSymbolicLink();
            boolean other = attributes.isOther();
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", name);
            map.put("hidden", String.valueOf(hidden));
            map.put("readable", String.valueOf(readable));
            map.put("writable", String.valueOf(writable));
            map.put("directory", String.valueOf(directory));
            map.put("modifytime", String.valueOf(modifytime));
            map.put("accesstime", String.valueOf(accesstime));
            map.put("creationtime", String.valueOf(creationtime));
            map.put("size", String.valueOf(size));
            map.put("regular", String.valueOf(regular));
            map.put("sybmoliclink", String.valueOf(sybmoliclink));
            map.put("other", String.valueOf(other));
            return map;
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
    }

    @Override
    public Set<Path> dir(Path path) {
        Condition.exist(path);
        Condition.readable(path);
        Condition.directory(path);

        try {
            return Files.list(path).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new FileServiceException(e);
        }
    }

    public static class Condition {

        public static void readable(Path path) {
            if(!Files.isReadable(path)) {
                throw new FileServiceUnreadableException();
            }
        }

        public static void writable(Path path) {
            if(!Files.isWritable(path)) {
                throw new FileServiceUnwritableException();
            }
        }

        public static void exist(Path path) {
            if(!Files.exists(path)) {
                throw new FileServiceNotFoundException();
            }
        }

        public static void unexist(Path path) {
            if(Files.exists(path)) {
                throw new FileServiceExistsException();
            }
        }

        public static void empty(Path path) {
            try {
                long count = Files.list(path).count();
                if(count > 0) {
                    throw new FileServiceNotEmptyException();
                }
            } catch (IOException e) {
                throw new FileServiceException(e);
            }
        }

        public static void directory(Path path) {
            if(!Files.isDirectory(path)) {
                throw new FileServiceIsFileException();
            }
        }

        public static void file(Path path) {
            if(Files.isDirectory(path)) {
                throw new FileServiceIsDirectoryException();
            }
        }
    }


}
