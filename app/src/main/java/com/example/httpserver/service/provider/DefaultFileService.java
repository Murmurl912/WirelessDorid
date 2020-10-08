package com.example.httpserver.service.provider;

import com.example.httpserver.service.FileService;
import org.apache.commons.io.FileUtils;

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

public class DefaultFileService implements FileService {
    @Override
    public Path rename(Path path, String name, String proxy) {
        Path destination = path.resolve(name);
        if(Files.exists(destination) && !proxy.equals("REPLACE")) {
            // destination already existed
            throw new RuntimeException();
        }
        return move(path, destination, proxy);
    }

    @Override
    public Path move(Path path, Path destination, String proxy) {
        if(Files.exists(destination) && !proxy.equals("REPLACE")) {
            // destination already existed
            throw new RuntimeException();
        }
        try {
            return Files.move(path, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void remove(Path path, String proxy) {
        if(!Files.exists(path)) {
            throw new RuntimeException();
        }
        if(Files.isDirectory(path)) {
            try {
                long count = Files.list(path).count();
                if(count > 0 && !"RECURSIVELY".equals(proxy)) {
                    // dir not empty
                    throw new RuntimeException();
                }
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Path copy(Path path, Path destination, String proxy) {
        Path result = destination;

        if(!Files.exists(path)) {
            // source not found
            throw new RuntimeException();
        }

        if(Files.exists(destination) && Files.isDirectory(path) == Files.isDirectory(destination)) {
            if("REPLACE".equals(proxy)) {
                try {
                    result = Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException();
            }
        }

        if(!Files.exists(destination)) {
            try {
                result = Files.copy(path, destination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    @Override
    public Path create(Path path, String proxy) {
        if(Files.exists(path)) {
            // conflict
            throw new RuntimeException();
        }
        Path result = path;
        try {
            result = Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    @Override
    public Path create(Path path, InputStream stream, String proxy) {
        if(Files.exists(path) && !proxy.equals("REPLACE") || Files.isDirectory(path)) {
            throw new RuntimeException();
        }
        try {
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return path;
    }

    @Override
    public void read(Path path, OutputStream stream) {
        if(!Files.exists(path) || Files.isDirectory(path)) {
            throw new RuntimeException();
        }
        try {
            Files.copy(path, stream);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Map<String, String> meta(Path path, String proxy) {
        if(!Files.exists(path)) {
            throw new RuntimeException();
        }
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
        try {
            BasicFileAttributes attributes = basicFileAttributeView.readAttributes();
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Path> dir(Path path) {
        if(!Files.isDirectory(path)) {
            throw new RuntimeException();
        }
        try {
            return Files.list(path).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Path> dirs(Path root) {
        try {
            return Files.walk(root).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
