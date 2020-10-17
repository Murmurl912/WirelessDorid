package com.example.httpserver.common.repository;

import com.example.httpserver.app.repository.FolderRepository;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.common.model.FileContext;

import java.util.List;
import java.util.stream.Collectors;

public class AndroidFileContextRepository implements FileContextRepository {

    private final FolderRepository repository;

    public AndroidFileContextRepository(FolderRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean contains(String context) {
        return repository.containName(context);
    }

    @Override
    public FileContext find(String context) {
        Folder folder = repository.getByName(context);
        if(folder == null) {
            return null;
        }

        return map(folder);
    }

    @Override
    public List<FileContext> all() {
        return repository.gets().stream().map(this::map).collect(Collectors.toList());
    }

    private FileContext map(Folder folder) {
        FileContext fc = new FileContext();
        fc.context = folder.name;
        fc.path = folder.path;
        fc.publicly = folder.publicly;
        fc.read = folder.read;
        fc.share = folder.share;
        fc.write = folder.write;
        return fc;
    }

}
