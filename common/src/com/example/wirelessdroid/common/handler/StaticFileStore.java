package com.example.wirelessdroid.common.handler;

import java.io.InputStream;

public interface StaticFileStore {

    public InputStream read(String uri);

}
