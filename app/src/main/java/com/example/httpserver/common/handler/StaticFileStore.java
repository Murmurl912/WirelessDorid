package com.example.httpserver.common.handler;

import java.io.InputStream;

public interface StaticFileStore {

    public InputStream read(String uri);

}
