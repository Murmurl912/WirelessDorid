package com.example.httpserver.app.service.http;

import android.content.res.AssetManager;
import com.example.httpserver.app.service.ServiceConfigurationRepository;
import com.example.httpserver.common.handler.*;
import com.example.httpserver.common.model.FileSystemView;
import com.example.httpserver.common.repository.TotpRepository;
import com.example.httpserver.common.route.Router;
import com.example.httpserver.common.service.AuthService;
import com.example.httpserver.common.service.SimpleFileService;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TinyHttpServer {

    public static final String KEY_HTTP_PORT = "http_port";
    public static final String KEY_HTTP_TIMEOUT = "http_timeout";
    public static final String DEFAULT_HTTP_PORT = "4443";
    private final ServiceConfigurationRepository repository;
    private final BiConsumer<Integer, Exception> DEFAULT_LISTENER = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {

        }
    };
    private HttpServer server;
    private AuthHandler authHandler;
    private SimpleFileService fileService;
    private AndroidFileHandler handler;
    private StaticFileHandler staticFileHandler;
    private FileSystemView view;
    private AuthService authService;
    private AssetManager assetManager;
    private StaticFileStore staticFileStore;
    private BiConsumer<Integer, Exception> listener = DEFAULT_LISTENER;


    public TinyHttpServer(ServiceConfigurationRepository repository, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.repository = repository;
    }

    private void init() throws InvalidKeySpecException, NoSuchAlgorithmException {

        int port = Integer.parseInt(repository.get(KEY_HTTP_PORT, DEFAULT_HTTP_PORT));
        authService = new AuthService(repository, TotpRepository.instance());
        authHandler = new AuthHandler(authService);

        view = new FileSystemView();
        fileService = new SimpleFileService(view);
        handler = new AndroidFileHandler(fileService, authService);
        staticFileStore = new AssetsStaticFileStore(assetManager);
        staticFileHandler = new StaticFileHandler(staticFileStore);
        server = new HttpServer(port);
        server.router
                .add(Router.Route.of("GET", "/api/time", (session, map) -> NanoHTTPD.newFixedLengthResponse(new Date().toString())))

                .add(Router.Route.of("POST", "/web-api/login", authHandler::login))
                .add(Router.Route.of("POST", "/web-api/logout", authHandler::logout))
                .add(Router.Route.of("POST", "/web-api/refresh", authHandler::refresh))

                .add(Router.Route.of("GET", "/fs-api", handler::root))
                .add(Router.Route.of("GET", "/fs-api/{*path}", handler::get))
                .add(Router.Route.of("COPY", "/fs-api/{*path}", handler::copy))
                .add(Router.Route.of("MOVE", "/fs-api/{*path}", handler::move))
                .add(Router.Route.of("PUT", "/fs-api/{*path}", handler::put))
                .add(Router.Route.of("DELETE", "/fs-api/{*path}", handler::delete))

                .add(Router.Route.of("GET", "/", staticFileHandler::index))
                .add(Router.Route.of("GET", "/web/{*path}", staticFileHandler::file));

    }

    public synchronized void start() {
        try {
            listener.accept(0, null);
            init();
            server.start();
            listener.accept(1, null);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            listener.accept(-1, e);
            // todo handle exception
        }
    }

    public synchronized void stop() {
        if (server != null) {
            listener.accept(2, null);
            server.stop();
            server = null;
        }
        listener.accept(3, null);
    }

    public void setServerListener(BiConsumer<Integer, Exception> listener) {
        this.listener = listener == null ? DEFAULT_LISTENER : listener;
    }

    public void removeServerListener() {
        this.listener = DEFAULT_LISTENER;
    }

    private static class HttpServer extends NanoHTTPD {

        private final Router<BiFunction<NanoHTTPD.IHTTPSession, Map<String, String>, NanoHTTPD.Response>> router;

        public HttpServer(int port) {
            super(port);
            router = new Router<>();
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                String uri = session.getUri();
                String method = session.getMethod().toString();
                HashMap<String, String> pathVariables = new HashMap<>();
                Router.Route<BiFunction<IHTTPSession, Map<String, String>, Response>> route = router.route(method, uri, pathVariables);
                if (route == null) {
                    // todo handle not found
                    return super.serve(session);
                }
                return route.handler.apply(session, pathVariables);
            } catch (Exception e) {
                // todo handle exception
                return super.serve(session);
            }
        }

    }


}
