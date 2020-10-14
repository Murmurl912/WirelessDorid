package com.example.httpserver.app.services.http.handler;

import com.example.httpserver.app.repository.FolderRepository;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.app.services.http.route.PathContainer;
import com.example.httpserver.app.services.http.route.PathPattern;
import com.example.httpserver.app.services.http.route.PathPatternParser;
import com.example.httpserver.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoFileUpload;
import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FileHandler {

    private final FileService service;
    private final FolderRepository folderRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileHandler(FileService service, FolderRepository repository) {
        this.service = service;
        folderRepository = repository;
    }

    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariable) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> ops = parameters.get("op");
        String op = "";
        if(ops != null && !ops.isEmpty()){
            op = ops.get(0);
        }

        if(!pathVariable.containsKey("context")) {
            return notfound(session.getUri());
        }
        String context = pathVariable.get("context");
        String path = pathVariable.getOrDefault("path", "");

        try {
            switch (op) {
                case "DIR":
                    return dir(context, path);
                case "META":
                    return meta(context, path);
                case "DOWNLOAD":
                    return download(context, path);
                default:
                    return defaultop(context, path);
            }
        } catch (Exception e) {
            return handle(session.getUri(), e);
        }
    }

    public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariable) {

        Map<String, List<String>> parameters = session.getParameters();
        List<String> proxies = parameters.get("proxies");
        String proxy = "";
        if(proxies != null && !proxies.isEmpty()) {
            proxy = proxies.get(0);
        }

        if(!pathVariable.containsKey("context")) {
            return notfound(session.getUri());
        }
        String context = pathVariable.get("context");
        String path = pathVariable.getOrDefault("path", "");

        try {
            boolean isMultipartContent = FileUploadBase.isMultipartContent(new NanoFileUpload.NanoHttpdContext(session));
            if(isMultipartContent) {
                return upload(context, path, proxy, session);
            } else {
                return mkdir(context, path);
            }
        } catch (Exception e) {
            return handle(session.getUri(), e);
        }
    }

    public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariable) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> proxies = parameters.get("proxies");
        String proxy = "";
        if(proxies != null && !proxies.isEmpty()) {
            proxy = proxies.get(0);
        }

        if(!pathVariable.containsKey("context")) {
            return notfound(session.getUri());
        }

        String context = pathVariable.get("context");
        String path = pathVariable.getOrDefault("path", "");
        try {
            return remove(context, path,proxy);
        } catch (Exception e) {
            return handle(session.getUri(), e);
        }

    }

    public NanoHTTPD.Response post(NanoHTTPD.IHTTPSession session, Map<String, String> pathVariable) {
        Map<String, List<String>> parameters = session.getParameters();
        List<String> ops = parameters.get("op");
        String op = "";
        if(ops != null && !ops.isEmpty()){
            op = ops.get(0);
        }
        List<String> proxies = parameters.get("proxy");
        String proxy = "";
        if(proxies != null && !proxies.isEmpty()){
            proxy = proxies.get(0);
        }
        List<String> destinations = parameters.get("proxy");
        String destination = "";
        if(destinations != null && !destinations.isEmpty()){
            destination = destinations.get(0);
        } else {
            return bad(session.getUri());
        }

        if(!pathVariable.containsKey("context")) {
            return notfound(session.getUri());
        }
        String sourceContext = pathVariable.get("context");
        String sourcePath = pathVariable.getOrDefault("path", "");

        try {
            PathPattern.PathMatchInfo info = new PathPatternParser().parse("/{context/{*path}")
                    .matchAndExtract(PathContainer.parsePath(destination));
            if(info == null) {
                return bad(session.getUri());
            }
            Map<String, String> destinationUriMap = info.getUriVariables();
            if(!destinationUriMap.containsKey("context")) {
                return bad(session.getUri());
            }
            String destinationContext = destinationUriMap.get("context");
            String destinationPath = destinationUriMap.get("path");

            switch (op) {
                case "COPY":
                    return copy(sourceContext, sourcePath, destinationContext, destinationPath, proxy);
                case "MOVE":
                    return move(sourceContext, sourcePath, destinationContext, destinationPath, proxy);
                default:
                    return bad(session.getUri());
            }
        } catch (Exception e) {
            return handle(session.getUri(), e);
        }
    }

    public NanoHTTPD.Response defaultop(String context, String path) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
        Path p = Paths.get(folder.path, path);
        if(Files.exists(p)) {
            if(Files.isDirectory(p)) {
                return dir(context, path);
            } else {
                return download(context, path);
            }
        } else {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
    }

    public NanoHTTPD.Response dir(String context, String path) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
        Set<Path> paths = service.dir(Paths.get(folder.path, path));
        List<Map<String, String>> metas =
                paths.stream().map(service::meta).collect(Collectors.toList());
        return json(metas);
    }

    public NanoHTTPD.Response meta(String context, String path) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
        Path p = Paths.get(folder.path, path);
        Map<String, String> meta = service.meta(p);
        return json(meta);
    }

    public NanoHTTPD.Response download(String context, String path) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
        Path p = Paths.get(folder.path, path);
        InputStream in = service.read(p);
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(p.getFileName().toString());
        return stream(contentType, in);
    }

    public NanoHTTPD.Response mkdir(String context, String path) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context).replaceAll("//", "/"));
        }
        Path p = Paths.get(folder.path, path);
        Path result = service.mkdir(p);

        return json(result);
    }

    public NanoHTTPD.Response upload(String context, String path, String proxy, NanoHTTPD.IHTTPSession session) {
        try {
            NanoFileUpload nanoFileUpload = new NanoFileUpload(new DiskFileItemFactory());
            FileItemIterator itemIterator = nanoFileUpload.getItemIterator(session);
            FileItemStream item = itemIterator.next();

            Folder folder = folder(context);
            if(folder == null) {
                return notfound(("/" + context).replaceAll("//", "/"));
            }
            Path p = Paths.get(folder.path, path);
            try {
                Path result = service.create(p, item.openStream(), proxy);
                // ignore rest of the file
                if(itemIterator.hasNext()) {
                    service.remove(p, "");
                    return bad(("/" + context + "/" + path).replaceAll("//", "/"));
                }
                return json(result);
            } catch (IOException e) {
                return handle(("/" + context + "/" + path).replaceAll("//", "/"), e);
            }
        } catch (FileUploadException | IOException e) {
            return handle(("/" + context + "/" + path).replaceAll("//", "/"), e);
        }
    }

    public NanoHTTPD.Response remove(String context, String path, String proxy) {
        Folder folder = folder(context);
        if(folder == null) {
            return notfound(("/" + context + "/" + path).replaceAll("//", "/"));
        }
        Path p = Paths.get(folder.path, path);
        service.remove(p, proxy);
        return ok();
    }

    public NanoHTTPD.Response copy(String sourceContext, String sourcePath, String destinationContext, String destinationPath, String proxy) {
        Folder source = folder(sourceContext);
        Folder target = folder(destinationContext);
        if(source == null) {
            return notfound(("/" + sourceContext).replaceAll("//", "/"));
        }
        if(target == null) {
            return notfound(("/" + destinationContext).replaceAll("//", "/"));
        }
        Path result = service.copy(Paths.get(source.path, sourcePath), Paths.get(target.path, destinationPath), proxy);
        return json(result);
    }

    public NanoHTTPD.Response move(String sourceContext, String sourcePath, String destinationContext, String destinationPath, String proxy) {
        Folder source = folder(sourceContext);
        Folder target = folder(destinationContext);
        if(source == null) {
            return notfound(("/" + sourceContext).replaceAll("//", "/"));
        }
        if(target == null) {
            return notfound(("/" + destinationContext).replaceAll("//", "/"));
        }
        Path result = service.move(Paths.get(source.path, sourcePath), Paths.get(target.path, destinationPath), proxy);
        return json(result);
    }

    private Folder folder(String context) {
        return folderRepository.getByName(context);
    }

    private NanoHTTPD.Response error(Exception e) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
    }

    private NanoHTTPD.Response json(Object o) {
        try {
            String data = mapper.writeValueAsString(o);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", data);
        } catch (JsonProcessingException e) {
            return error(e);
        }
    }

    private NanoHTTPD.Response stream(String mimeTypes, InputStream stream) {
        return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, mimeTypes, stream);
    }

    private NanoHTTPD.Response bad(String uri) {
        return json(NanoHTTPD.Response.Status.BAD_REQUEST, ErrorModel.of(2, 403, uri, "", null));
    }

    private NanoHTTPD.Response ok() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "");
    }

    private NanoHTTPD.Response json(NanoHTTPD.Response.IStatus status, Object message) {
        try {
            String data = mapper.writeValueAsString(message);
            return NanoHTTPD.newFixedLengthResponse(status, "application/json", data);
        } catch (JsonProcessingException e) {
            return error(e);
        }
    }

    private NanoHTTPD.Response notfound(String uri) {
        return json(NanoHTTPD.Response.Status.NOT_FOUND, ErrorModel.of(1, 404, uri, "uri resourse not found", null));
    }

    private NanoHTTPD.Response internal(Exception e) {
        try {
            String data = mapper.writeValueAsString(e);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", data);
        } catch (JsonProcessingException ex) {
            return error(e);
        }
    }

    private NanoHTTPD.Response handle(String uri, Exception e) {
        if(e instanceof FileServiceException) {
            FileServiceErrorModel model = FileServiceErrorModel.of(uri, (FileServiceException) e);
            return json(NanoHTTPD.Response.Status.lookup(model.status), model);
        } else {
            ErrorModel model = new ErrorModel();
            return json(NanoHTTPD.Response.Status.INTERNAL_ERROR, model);
        }
    }
}
