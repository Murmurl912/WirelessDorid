package com.example.httpserver.service.impl;

import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.app.services.http.route.PathContainer;
import com.example.httpserver.app.services.http.route.PathPattern;
import com.example.httpserver.app.services.http.route.PathPatternParser;
import com.example.httpserver.service.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoFileUpload;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class HttpFileHandler implements FileHandler {

    private final FileService fileService;
    private final ContextService contextService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MimetypesFileTypeMap types;

    public HttpFileHandler(FileService fileService, ContextService contextService) {
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        this.fileService = fileService;
        this.contextService = contextService;
        this.types = new MimetypesFileTypeMap();
    }

    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");

        FileMetaData meta = meta(uri,context, path);
        NanoHTTPD.Response response;

        if(meta.directory) {
            List<FileMetaData> body = dir(uri, context, path);
            response = ok(body);
        } else {
            InputStream stream = read(uri, context, path);
            response = ok(types.getContentType(path), stream);
        }
        response.addHeader("meta", toJson(meta));
        return response;

    }

    public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        Map<String, String> headers = session.getHeaders();
        String override = headers.get("override");
        String proxy = override != null && override.equals("true") ? "replace" : "";

        boolean isMultipartContent = FileUploadBase.isMultipartContent(new NanoFileUpload.NanoHttpdContext(session));
        FileMetaData meta = null;
        NanoHTTPD.Response response;

        if (isMultipartContent) {
            NanoFileUpload nanoFileUpload = new NanoFileUpload(new DiskFileItemFactory());

            FileItemIterator itemIterator;
            try {
                itemIterator = nanoFileUpload.getItemIterator(session);
                FileItemStream item = itemIterator.next();
                meta = write(uri, context, path, item.openStream(), proxy);
                response = created();
            } catch (FileUploadException | IOException e) {
                return error(ErrorResponseModel.error(uri, e));
            }
        } else {
            // create dir
            meta = mkdir(uri, context, path);
            response = created();
        }

        response.addHeader("meta", toJson(meta));
        return response;
    }

    public NanoHTTPD.Response move(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        Map<String, String> headers = session.getHeaders();
        String destination = headers.get("destination");
        String override = headers.get("override");
        String proxy = override != null && override.equals("true") ? "replace" : "";

        if(destination == null || destination.isEmpty()) {
            return bad(); // destination not specified
        }
        if(path == null || path.isEmpty()) {
            return bad(); // source path is root
        }
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/{context}/{*path}")
                .matchAndExtract(PathContainer.parsePath(destination));
        if(info == null) {
            return bad(); // destination uri not valid
        }
        String destinationContext = info.getUriVariables().get("context");
        String destinationPath = info.getUriVariables().get("path");
        if(destinationPath == null || destinationPath.isEmpty()) {
            return bad(); // destination uri is root
        }

        FileMetaData meta = move(uri, proxy, context, path, destinationContext, destinationPath);
        NanoHTTPD.Response response = created();
        response.addHeader("meta", toJson(meta));
        return response;
    }

    public NanoHTTPD.Response copy(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        Map<String, String> headers = session.getHeaders();
        String destination = headers.get("destination");
        String override = headers.get("override");
        String proxy = override != null && override.equals("true") ? "replace" : "";

        if(destination == null || destination.isEmpty()) {
            return bad(); // destination not specified
        }
        if(path == null || path.isEmpty()) {
            return bad(); // source path is root
        }
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/{context}/{*path}")
                .matchAndExtract(PathContainer.parsePath(destination));
        if(info == null) {
            return bad(); // destination uri not valid
        }
        String destinationContext = info.getUriVariables().get("context");
        String destinationPath = info.getUriVariables().get("path");
        if(destinationPath == null || destinationPath.isEmpty()) {
            return bad(); // destination uri is root
        }

        FileMetaData meta = copy(uri, proxy, context, path, destinationContext, destinationPath);
        NanoHTTPD.Response response = created();
        response.addHeader("meta", toJson(meta));
        return response;
    }

    public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        Map<String, String> headers = session.getHeaders();
        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        String depth = headers.get("depth");

        if(path == null || path.isEmpty()) {
            return bad(); // is root
        }
        String proxy = depth != null && depth.equals("infinity") ? "recursive" : "";
        delete(uri, proxy, context, path);
        return nocontent();
    }

    private NanoHTTPD.Response ok(Object data) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", toJson(data));
    }

    private NanoHTTPD.Response ok(String type, InputStream stream) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, type, stream);
    }

    private NanoHTTPD.Response created() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.CREATED, "application/json", null);
    }

    private NanoHTTPD.Response nocontent() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT, "application/json", null);
    }

    private NanoHTTPD.Response bad() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", null);
    }

    private NanoHTTPD.Response bad(Object message) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", toJson(message));
    }

    private NanoHTTPD.Response error(Object message) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", toJson(message));
    }

    private String toJson(Object model) {
        try {
            return mapper.writeValueAsString(model);
        } catch (JsonProcessingException ignored) {

        }

        return model.toString();
    }

    @Override
    public FileService fileService() {
        return fileService;
    }

    @Override
    public ContextService contextService() {
        return contextService;
    }
}
