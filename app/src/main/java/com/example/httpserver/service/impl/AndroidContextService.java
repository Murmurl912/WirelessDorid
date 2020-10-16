package com.example.httpserver.service.impl;

import com.example.httpserver.app.repository.FolderRepository;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.service.ContextService;
import com.example.httpserver.service.ContextServiceAccessException;
import com.example.httpserver.service.ContextServiceContextNotFoundException;
import com.example.httpserver.service.ContextServiceException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AndroidContextService implements ContextService {

    private FolderRepository repository;

    public AndroidContextService(FolderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> context() {
        return repository.gets()
                .stream()
                .filter(folder -> folder.share)
                .map(folder -> folder.name)
                .collect(Collectors.toList());
    }

    @Override
    public Path access(String context, String path, ContextServiceAction... actions) {
        Folder folder = repository.getByName(context);
        if(folder == null) {
            throw new ContextServiceContextNotFoundException("Context is not found", ContextServiceException.CONDE_CONTEXT_NOT_FOUND, context, path);
        }
        if(!folder.share) {
            throw new ContextServiceAccessException("Context is not shared", ContextServiceException.CODE_CONTEXT_FORBIDDEN, context, path);
        }

        for (ContextServiceAction action : actions) {
            switch (action) {
                case CONTEXT_SERVICE_ACTION_META:
                case CONTEXT_SERVICE_ACTION_READ:
                    if(folder.read) {
                        continue;
                    } else {
                        throw new ContextServiceAccessException("Context is not readable", ContextServiceException.CODE_CONTEXT_FORBIDDEN, context, path);
                    }
                case CONTEXT_SERVICE_ACTION_TOUCH:
                case CONTEXT_SERVICE_ACTION_MKDIR:
                case CONTEXT_SERVICE_ACTION_MODIFY:
                case CONTEXT_SERVICE_ACTION_WRITE:
                    if(folder.write) {
                        continue;
                    } else {
                        throw new ContextServiceAccessException("Context is not writable", ContextServiceException.CODE_CONTEXT_FORBIDDEN, context, path);
                    }
                default:
                    throw new ContextServiceAccessException("Unknown action: " + action, ContextServiceException.CODE_CONTEXT_FORBIDDEN, context, path);
            }
        }

        return Paths.get(folder.path, path);
    }

}
