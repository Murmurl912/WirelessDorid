package com.example.httpserver.app.services;

import android.util.Log;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.ConfigurationRepository;
import com.example.httpserver.app.repository.FolderRepository;
import com.example.httpserver.app.repository.TimeBasedOneTimePassword;
import com.example.httpserver.app.repository.TotpRepository;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.service.AndroidFileService;
import com.example.httpserver.service.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class WebHttpServer extends NanoHTTPD {

    private final ConcurrentHashMap<String, Map<String, ?>> sessionStore = new ConcurrentHashMap<>();
    private TimeBasedOneTimePassword totp;
    private FolderRepository folderRepository;
    private ConfigurationRepository configurationRepository;
    private ObjectMapper mapper = new ObjectMapper();
    public static final String TAG = WebHttpServer.class.getName();
    private int port = 8080;

    private FileService fileService;

    public WebHttpServer() {
        super(8080);
        try {
            totp = TotpRepository.instance().getDefault();
        } catch (NoSuchAlgorithmException e) {
            totp = null;
        }

        folderRepository = App.app().db().folder();
        configurationRepository = App.app().db().configuration();
        fileService = new AndroidFileService();
    }

    @Override
    public Response serve(IHTTPSession session) {
        log(session);
        boolean result = filter(session);
        if(!result) {
            return newFixedLengthResponse(Response.Status.NO_CONTENT, "text/plain", null);
        }
        return dispatch(session);
    }

    private Response dispatch(IHTTPSession session) {
        List<String> paths = new ArrayList<>(Arrays.asList(session.getUri().split("/")));
        String context = "";
        if(paths.size() > 1) {
            paths.remove(0);
            context = paths.remove(0);
        }
        return handle(context, paths, session);
    }

    private boolean filter(IHTTPSession session) {
        String id = session.getCookies().read("sessionid");
        if(id == null || id.equals("")) {
            Cookie cookie = new Cookie("sessionid", UUID.randomUUID().toString(), "session");
            session.getCookies().set(cookie);
        }
        return true;
    }

    private String log(IHTTPSession session) {
        String uri = session.getUri();
        String method = session.getMethod().name();
        String parameters = session.getParameters().toString();
        String sessionid = session.getCookies().read("sessionid");
        Map<String, Object> json = new HashMap<>();
        json.put("uri", session.getUri());
        json.put("method", session.getMethod());
        json.put("parameters", session.getParameters());
        json.put("session", sessionid);
        json.put("headers", session.getHeaders());
        json.put("path", uri.split("/"));
        Log.d(TAG, "" + json);
        try {
            return mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return json.toString();
        }
    }

    private Response handle(String context, List<String> path, IHTTPSession session) {

        if(context.equals("web")) {
            return web(context, path, session);
        }

        Folder f = folderRepository.getByName(context);
        if(f == null) {
            return notFound("Context: " + context + " does not exist", session);
        }

        return handle(f, context, path, session);
    }

    private Response handle(Folder f, String context, List<String> path, IHTTPSession session) {
        String filePath = session.getUri().replace("/" + context, "");
        String fullPath = f.path + filePath;

        return handle(f, fullPath, session);
    }

    private Response handle(Folder f, String fullpath, IHTTPSession session) {
        File file = new File(fullpath);

        switch (session.getMethod()) {
            case GET:
               return get(f, file, session);
            case PUT:
                break;
            case POST:
                break;
            case DELETE:
                break;
            default: super.serve(session);
        }

        return super.serve(session);
    }

    private Response web(String context, List<String> path, IHTTPSession session) {
        return newFixedLengthResponse("Web");
    }

    private Response get(Folder dir, File file, IHTTPSession session) {
        if(!file.exists()) {
            return notFound("File: " + file.toString() + " is not found", session);
        }
        if(file.isDirectory()) {
            try {
                return newFixedLengthResponse(Response.Status.OK, "application/json", mapper.writeValueAsString(file.listFiles()));
            } catch (JsonProcessingException e) {
                return error(e.getMessage(), session);
            }
        }
        try {
            return newFixedLengthResponse(Response.Status.OK, "text/plain", mapper.writeValueAsString(file));
        } catch (JsonProcessingException e) {
            return error(e.getMessage(), session);
        }
    }

    private Response notFound(String message, IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", message);
    }

    private Response error(String message, IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", message);
    }
}