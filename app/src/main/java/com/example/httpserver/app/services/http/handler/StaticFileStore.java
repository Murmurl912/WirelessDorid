package com.example.httpserver.app.services.http.handler;

import java.io.InputStream;

public interface StaticFileStore {

    public InputStream read(String uri);

}
