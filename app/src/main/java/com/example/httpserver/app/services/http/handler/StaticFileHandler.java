package com.example.httpserver.app.services.http.handler;

import fi.iki.elonen.NanoHTTPD;

import javax.activation.MimetypesFileTypeMap;
import java.util.Map;

public class StaticFileHandler {
    private StaticFileStore fileStore;

    public StaticFileHandler(StaticFileStore store) {
        this.fileStore = store;
    }

    public NanoHTTPD.Response file(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariables) {
        try {
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeTypesMap.getContentType(session.getUri()), fileStore.read(pathVariables.get("path")));
        } catch (Exception e) {
            return notfound(session.getUri());
        }
    }

    public NanoHTTPD.Response index(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariables) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, "text/html", fileStore.read("index.html"));
    }

    private NanoHTTPD.Response notfound(String uri) {
        return null;
    }

}
