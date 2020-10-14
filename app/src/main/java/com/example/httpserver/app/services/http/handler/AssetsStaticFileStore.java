package com.example.httpserver.app.services.http.handler;

import android.content.res.AssetManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class AssetsStaticFileStore implements StaticFileStore {
    private AssetManager manager;

    public AssetsStaticFileStore(AssetManager manager) {
        this.manager = manager;
    }

    @Override
    public InputStream read(String uri) {
        try {
            return manager.open(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
