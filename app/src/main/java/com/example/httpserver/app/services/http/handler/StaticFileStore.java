package com.example.httpserver.app.services.http.handler;

import java.io.InputStream;
import java.nio.file.Path;

public interface StaticFileStore {

    public InputStream read(String uri);

}
