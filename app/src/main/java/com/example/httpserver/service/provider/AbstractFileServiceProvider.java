package com.example.httpserver.service.provider;

import com.example.httpserver.service.AccessControlService;
import com.example.httpserver.service.AuthenticationService;
import com.example.httpserver.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFileServiceProvider implements FileService {

    private AccessControlService<Path, FileAccessAction, String>  control;
    private AuthenticationService auth;
    private FileService service;

    private String principle;
    private Map<String, ?> info;

    public abstract String principle();

    public abstract Map<String, ?> info();

    public abstract AccessControlService<Path, FileAccessAction, String> control();

    public abstract AuthenticationService authentication();


    private void auth() {
        boolean authResult = false;
        try {
            authResult = authentication().authenticate(info());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!authResult) {
            throw new RuntimeException();
        }
    }

    private void canWrite(Path path) {
        if(!control().access(path, FileAccessAction.ACTION_WRITE)) {
            throw new RuntimeException();
        };
    }

    private void canRead(Path path) {
        if(!control().access(path, FileAccessAction.ACTION_READ)) {
            throw new RuntimeException();
        };
    }

    @Override
    public Path rename(Path path, String name, String proxy) {
        auth();
        canWrite(path);
        Path after = path.resolve(name);
        try {
            if(Files.isSameFile(path.getParent(), after.getParent())) {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        canWrite(after);

        return service.rename(path, name, proxy);
    }

    @Override
    public Path move(Path path, Path destination, String proxy) {
        auth();
        canWrite(path);
        canWrite(destination);
        return service.move(path, destination, proxy);
    }

    @Override
    public void remove(Path path, String proxy) {
        auth();
        canWrite(path);
        service.remove(path, proxy);
    }

    @Override
    public Path copy(Path path, Path destination, String proxy) {
        auth();
        canRead(path);
        canWrite(destination);
        return service.copy(path, destination, proxy);
    }

    @Override
    public Path touch(Path path) {
        auth();
        canWrite(path);
        return service.touch(path);
    }

    @Override
    public Path create(Path path, InputStream stream, String proxy) {
        auth();
        canWrite(path);
        return service.create(path, stream, proxy);
    }

    @Override
    public void write(Path path, OutputStream stream) {
        auth();
        canRead(path);
        service.write(path, stream);
    }

    @Override
    public Map<String, String> meta(Path path) {
        auth();
        canRead(path);
        return service.meta(path);
    }

    @Override
    public Set<Path> dir(Path path) {
        auth();
        canRead(path);
        return service.dir(path);
    }

}
