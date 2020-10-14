package com.example.httpserver.app.services.http.handler;

import com.example.httpserver.service.FileService;
import fi.iki.elonen.NanoHTTPD;

import java.util.List;
import java.util.Map;

public class FileHandler {

    private FileService service;

    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariable) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> ops = parameters.get("op");
        String op = "DOWNLOAD";
        if(ops != null && !ops.isEmpty()){
            op = ops.get(0);
        }

        if(pathVariable.containsKey("context")) {

        }
        switch (op) {
            case "DIR":
                break;
            case "META":
                break;
            case "THUMB":
                break;
            case "DOWNLOAD":
            default:

        }

        return null;
    }



}
