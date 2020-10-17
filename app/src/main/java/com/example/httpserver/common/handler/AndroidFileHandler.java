package com.example.httpserver.common.handler;

import com.example.httpserver.app.services.http.FileMetaData;
import com.example.httpserver.app.services.http.route.PathContainer;
import com.example.httpserver.app.services.http.route.PathPattern;
import com.example.httpserver.app.services.http.route.PathPatternParser;
import com.example.httpserver.common.model.FileData;
import com.example.httpserver.common.repository.FileContextRepository;
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
import javax.net.ssl.SSLEngineResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AndroidFileHandler {

    private final FileService service;
    private final ObjectMapper mapper;
    private final FileContextRepository repository;
    private MimetypesFileTypeMap map  = new MimetypesFileTypeMap();
    public AndroidFileHandler(FileService service, ObjectMapper mapper, FileContextRepository repository) {
        this.service = service;
        this.mapper = mapper;
        this.repository = repository;
    }

    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
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
                NanoHTTPD.Response response = ok(meta);
                response.addHeader("meta", json(meta));
                return response;
            } else {
                String type = map.getContentType(data.path);
                InputStream stream = service.read(data);
                NanoHTTPD.Response response = ok(type, stream);
                response.addHeader("meta", json(meta));
                return response;
            }
        } catch (FileNotFoundException e) {
            return notfound(e);
        } catch (SecurityException e) {
            return forbidden(e);
        } catch (IOException e) {
            return bad(e);
        }
    }

    public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
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
            } catch (SecurityException e) {
                return forbidden(e);
            } catch (FileAlreadyExistsException | DirectoryNotEmptyException e) {
                return bad(e);
            } catch (FileUploadException | IOException e) {
                return bad(e);
            }
        } else {
            try {
                meta = service.mkdir(data);
                response = created(meta);
            } catch (SecurityException e) {
                return forbidden(e);
            } catch (FileAlreadyExistsException e) {
                return bad(e);
            } catch (IOException e) {
                return bad(e);
            }
        }

        response.addHeader("meta", json(meta));
        return response;
    }

    public NanoHTTPD.Response move(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
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
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/{context}/{*path}")
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
        } catch (SecurityException e) {
            return forbidden(e);
        } catch (FileNotFoundException e) {
            return notfound(e);
        } catch (DirectoryNotEmptyException e) {
            return bad(e);
        } catch (FileAlreadyExistsException e) {
            return bad(e);
        } catch (IOException e) {
            return bad(e);
        }

    }

    public NanoHTTPD.Response copy(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
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
        PathPattern.PathMatchInfo info = new PathPatternParser().parse("/{context}/{*path}")
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
        } catch (SecurityException e) {
            return forbidden(e);
        } catch (FileNotFoundException e) {
            return notfound(e);
        } catch (DirectoryNotEmptyException e) {
            return bad(e);
        } catch (FileAlreadyExistsException e) {
            return bad(e);
        } catch (IOException e) {
            return bad(e);
        }



    }

    public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
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
        } catch (SecurityException e) {
            return forbidden(e);
        } catch (DirectoryNotEmptyException e) {
            return bad(e);
        } catch (IOException e) {
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
    private NanoHTTPD.Response bad(Object message) {
        return json(NanoHTTPD.Response.Status.BAD_REQUEST, message);
    }
    private NanoHTTPD.Response forbidden(Object message) {
        return json(NanoHTTPD.Response.Status.FORBIDDEN, message);
    }

    private NanoHTTPD.Response unauthorized(Object message) {
        return json(NanoHTTPD.Response.Status.UNAUTHORIZED, message);
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
