package com.example.httpserver.common.handler;

import android.util.Log;
import com.example.httpserver.common.exception.*;
import com.example.httpserver.common.model.FileMetaData;
import com.example.httpserver.common.model.ResponseModel;
import com.example.httpserver.common.server.route.PathContainer;
import com.example.httpserver.common.server.route.PathPattern;
import com.example.httpserver.common.server.route.PathPatternParser;
import com.example.httpserver.common.model.FileData;
import com.example.httpserver.common.service.AuthService;
import com.example.httpserver.common.service.FileService;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AndroidFileHandler {

    public static final String TAG = AndroidFileHandler.class.getSimpleName();

    private final FileService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MimetypesFileTypeMap map  = new MimetypesFileTypeMap();
    private final AuthService authService;

    public AndroidFileHandler(FileService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    public NanoHTTPD.Response root(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        Log.i(TAG, "Request received: " + session.getMethod() + " " + session.getUri());
        return ok(service.root());
    }

    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        NanoHTTPD.Response authResponse = auth(session);
        if(authResponse != null) {
            return authResponse;
        }

        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        FileData data = new FileData();
        data.path = path;
        data.context = context;
        data.uri = uri;


        try {
            FileMetaData meta = service.meta(data);
            if(meta.directory) {
                List<FileMetaData> metas = service.dir(data);
                NanoHTTPD.Response response = ok(metas);
                response.addHeader("meta", json(meta));
                return response;
            } else {
                String type = map.getContentType(data.path);
                InputStream stream = service.read(data);
                NanoHTTPD.Response response = ok(type, stream);
                response.addHeader("meta", json(meta));
                return response;
            }
        } catch (PathException e) {
            return bad(e);
        }
    }

    public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        NanoHTTPD.Response authResponse = auth(session);
        if(authResponse != null) {
            return authResponse;
        }


        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        Map<String, String> headers = session.getHeaders();

        FileData data = new FileData();
        data.path = path;
        data.context = context;
        data.uri = uri;
        data.override = Boolean.parseBoolean(headers.getOrDefault("override", "false"));

        boolean isMultipartContent = FileUploadBase.isMultipartContent(new NanoFileUpload.NanoHttpdContext(session));
        FileMetaData meta = null;
        NanoHTTPD.Response response;

        if (isMultipartContent) {
            NanoFileUpload nanoFileUpload = new NanoFileUpload(new DiskFileItemFactory());

            FileItemIterator itemIterator;
            try {
                itemIterator = nanoFileUpload.getItemIterator(session);
                FileItemStream item = itemIterator.next();
                meta = service.write(data, item.openStream());
                response = created(meta);
            } catch (PathException e) {
                return bad(e);
            } catch (Exception e) {
                return error(e);
            }
        } else {
            try {
                meta = service.mkdir(data);
                response = created(meta);
            } catch (PathException e) {
                return bad(e);
            }
        }

        response.addHeader("meta", json(meta));
        return response;
    }

    public NanoHTTPD.Response move(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        NanoHTTPD.Response authResponse = auth(session);
        if(authResponse != null) {
            return authResponse;
        }

        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");

        FileData source = new FileData();
        source.path = path;
        source.context = context;
        source.uri = uri;

        FileData destination = new FileData();
        destination.uri = session.getHeaders().getOrDefault("destination", "");
        destination.override = Boolean.parseBoolean(session.getHeaders().getOrDefault("override", "false"));
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/fs-api/{context}/{*path}")
                .matchAndExtract(PathContainer.parsePath(destination.uri));
        if(info != null) {
            destination.context = info.getUriVariables().get("context");
            destination.path = info.getUriVariables().get("path");
        } else {
            destination.context = "";
            destination.path = "";
        }

        try {
            FileMetaData metaData = service.move(source, destination);
            NanoHTTPD.Response response = ok(metaData);
            response.addHeader("meta", json(metaData));
            return response;
        } catch (PathException e) {
            return bad(e);
        }
    }

    public NanoHTTPD.Response copy(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        NanoHTTPD.Response authResponse = auth(session);
        if(authResponse != null) {
            return authResponse;
        }

        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");

        FileData source = new FileData();
        source.path = path;
        source.context = context;
        source.uri = uri;

        FileData destination = new FileData();
        destination.uri = session.getHeaders().getOrDefault("destination", "");
        destination.override = Boolean.parseBoolean(session.getHeaders().getOrDefault("override", "false"));
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/fs-api/{context}/{*path}")
                .matchAndExtract(PathContainer.parsePath(destination.uri));
        if(info != null) {
            destination.context = info.getUriVariables().get("context");
            destination.path = info.getUriVariables().get("path");
        } else {
            destination.context = "";
            destination.path = "";
        }

        try {
            FileMetaData metaData = service.copy(source, destination);
            NanoHTTPD.Response response = ok(metaData);
            response.addHeader("meta", json(metaData));
            return response;
        } catch (PathException e) {
            return bad(e);
        }

    }

    public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        NanoHTTPD.Response authResponse = auth(session);
        if(authResponse != null) {
            return authResponse;
        }


        String uri = session.getUri();
        String context = vars.get("context");
        String path = vars.get("path");
        Map<String, String> headers = session.getHeaders();

        FileData data = new FileData();
        data.path = path;
        data.context = context;
        data.uri = uri;
        data.recursive = Boolean.parseBoolean(headers.getOrDefault("recursive", "false"));

        try {
            service.remove(data);
            return nocontent();
        } catch (PathException e) {
            return bad(e);
        }
    }

    private NanoHTTPD.Response ok(Object message) {
        return json(NanoHTTPD.Response.Status.OK, message);
    }

    private NanoHTTPD.Response created(Object message) {
        return json(NanoHTTPD.Response.Status.CREATED, message);
    }

    private NanoHTTPD.Response nocontent() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT, "", "");
    }

    private NanoHTTPD.Response bad(PathException ex) {

        Log.i(TAG, "Bad request ", ex);

        ResponseModel model = new ResponseModel();
        model.error = ex;
        model.timestamp = new Date();

        if(ex instanceof PathExistsException) {
            model.code = 1;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + " already exists";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathIsDirectory) {
            model.code = 2;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + ", is a directory";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathIsEmpty) {
            model.code = 3;
            model.status = 400;
            model.message = "context cannot be modified, context: " + ex.getSource().context;
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathIsFile) {
            model.code = 4;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + ", is a file";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathNotEmpty) {
            model.code = 5;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + ", path is a directory and not empty";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathNotFound) {
            model.code = 6;
            model.status = 404;
            model.message = "source path: " + ex.getSource().uri + ", is not found";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathDeniedException) {
            model.code = 7;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + ", access is denied by os";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathNotReadable) {
            model.code  = 8;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + " cannot be read";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof  PathNotWritable) {
            model.code = 9;
            model.status = 400;
            model.message = "source path: " + ex.getSource().uri + " cannot be write";
            model.uri = ex.getSource().uri;
        } else if(ex instanceof PathContextNotFound){
            model.code = 10;
            model.status = 404;
            model.message = "context: " + ex.getSource().context + " cannot be found";
            model.uri = ex.getSource().uri;
        } else {
            model.code = 11;
            model.status = 400;
            model.message = "error occurs during handle request";
            model.uri = ex.getSource().uri;
        }

        return json(NanoHTTPD.Response.Status.lookup(model.status), model);
    }

    private NanoHTTPD.Response error(Exception e) {
        Log.i(TAG, "internal error ", e);
        return json(NanoHTTPD.Response.Status.INTERNAL_ERROR, e);
    }

    private NanoHTTPD.Response forbidden(Object message) {
        return json(NanoHTTPD.Response.Status.FORBIDDEN, message);
    }

    private NanoHTTPD.Response unauthorized(String uri, Exception e) {
        ResponseModel model = new ResponseModel();
        model.message = "Token is invalid or expired";
        model.code = 9;
        model.status = 401;
        model.uri = uri;
        model.timestamp = new Date();

        return json(NanoHTTPD.Response.Status.UNAUTHORIZED, model);
    }

    private NanoHTTPD.Response auth(NanoHTTPD.IHTTPSession session) {
        try {
            authService.verify(session);
            return null;
        } catch (Exception e) {
            return unauthorized(session.getUri(), e);
        }
    }

    private NanoHTTPD.Response notfound(Object message) {
        return json(NanoHTTPD.Response.Status.NOT_FOUND, message);
    }

    private NanoHTTPD.Response ok(String type, InputStream stream) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, type, stream);
    }

    private String json(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return o.toString();
        }
    }

    private NanoHTTPD.Response json(NanoHTTPD.Response.Status status, Object o) {
        return NanoHTTPD.newFixedLengthResponse(status, "application/json", json(o));
    }
}
