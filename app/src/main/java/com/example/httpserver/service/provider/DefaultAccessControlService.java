package com.example.httpserver.service.provider;

import com.example.httpserver.service.AccessControlService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultAccessControlService implements AccessControlService<Path, FileAccessAction, String> {

    private ConcurrentHashMap<Path, FilePermission> rules;
    private final FilePermission DEFAULT = new FilePermission();
    private String principle = "default";

    @Override
    public String principle() {
        return principle;
    }

    @Override
    public boolean access(Path path, FileAccessAction fileAccessAction) {
        FilePermission permission = search(path);
        switch (fileAccessAction) {
            case ACTION_READ:
                return permission.read;
            case ACTION_WRITE:
                return permission.write;
            default:
                return false;
        }
    }

    private FilePermission search(Path path) {
        if(rules.containsKey(path)) {
            return rules.get(path);
        }

        for(Map.Entry<Path, FilePermission> entry : rules.entrySet()) {
            if(isDirectChild(entry.getKey(), path)) {
                return entry.getValue();
            }
            if(isChild(entry.getKey(), path)) {
                FilePermission permission = entry.getValue();
                if(permission.recursive) {
                    return permission;
                } else {
                    return DEFAULT;
                }
            }
        }
        return DEFAULT;
    }

    public static boolean isChild(Path parent, Path target) {
        try {
            parent = parent.toAbsolutePath();
            target = target.toAbsolutePath();
            int parentNameCount = parent.getNameCount();
            int targetNameCount = target.getNameCount();

            if(parentNameCount > targetNameCount) {
                return false;
            }
            for(int i = 0; i < parentNameCount; i++) {
                if(!parent.getName(i).equals(target.getName(i))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDirectChild(Path parent, Path target) {
        try {
            parent = parent.toAbsolutePath();
            target = target.toAbsolutePath();
            return Files.isSameFile(parent, target.getParent());
        } catch (IOException e) {
            return false;
        }
    }

}
