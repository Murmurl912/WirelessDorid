package com.example.httpserver.common.repository;

import com.example.httpserver.common.model.FileContext;

import java.util.List;

public interface FileContextRepository {

    public boolean contains(String context);

    public FileContext find(String context);

    public List<FileContext> all();
}
