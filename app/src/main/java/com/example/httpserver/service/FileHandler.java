package com.example.httpserver.service;

import com.example.httpserver.app.services.http.FileMetaData;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public interface FileHandler {

    public FileService fileService();

    public ContextService contextService();

    default public List<String> root() {
        return contextService().context();
    }

    default public FileMetaData meta(String uri, String context, String path) {
        Path file = contextService().access(context, path,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_META);
        return fileService().meta(uri, file);
    }

    default public List<FileMetaData> dir(String uri, String context, String path) {
        Path dir = contextService().access(context, path,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ);
        return fileService().dir(uri, dir);
    }

    default public FileMetaData move(String uri, String proxy,
                                     String sourceContext, String sourcePath,
                                     String targetContext, String targetPath) {
        Path source = contextService().access(sourceContext, sourcePath,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        Path target = contextService().access(targetContext, targetPath,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        Path destination =  fileService().move(source, target, proxy);
        return FileMetaData.from(targetContext + "/" + targetPath, destination);
    }

    default public void delete(String uri, String proxy, String context, String path) {
        Path file = contextService().access(context, path,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        fileService().remove(file, proxy);
    }

    default public FileMetaData copy(String uri, String proxy,
                                     String sourceContext, String sourcePath,
                                     String targetContext, String targetPath) {
        Path source = contextService().access(sourceContext, sourcePath,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        Path target = contextService().access(targetContext, targetPath,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        Path destination =  fileService().copy(source, target, proxy);
        return FileMetaData.from(targetContext + "/" + targetPath, destination);
    }

    default public InputStream read(String uri, String context, String path) {
        Path file = contextService().access(context, path,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_READ);
        return fileService().read(file);
    }

    default public FileMetaData mkdir(String uri, String context, String path) {
        Path dir = contextService().access(context, path,
                ContextService.ContextServiceAction.CONTEXT_SERVICE_ACTION_WRITE);
        Path result = fileService().mkdir(dir);
        return FileMetaData.from(uri, result);
    }

    default public FileMetaData write(String uri, String context, String path, InputStream stream, String proxy) {
        return null;
    }
}
