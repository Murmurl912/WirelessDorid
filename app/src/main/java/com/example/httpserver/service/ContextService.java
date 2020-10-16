package com.example.httpserver.service;

import java.nio.file.Path;
import java.util.List;

public interface ContextService {

    public enum ContextServiceAction {
        CONTEXT_SERVICE_ACTION_META,
        CONTEXT_SERVICE_ACTION_MODIFY,
        CONTEXT_SERVICE_ACTION_MKDIR,
        CONTEXT_SERVICE_ACTION_TOUCH,
        CONTEXT_SERVICE_ACTION_READ,
        CONTEXT_SERVICE_ACTION_WRITE,
        CONTEXT_SERVICE_ACTION_REMOVE
    }

    public List<String> context();

    public Path access(String context, String path, ContextServiceAction...actions);

    public class WebContext {
        public String context;
        public String path;
        public boolean read;
        public boolean write;
        public boolean delete;
        public boolean publicly;
    }
}
