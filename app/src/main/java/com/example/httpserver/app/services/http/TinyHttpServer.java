package com.example.httpserver.app.services.http;

import com.example.httpserver.app.services.ServiceConfigurationRepository;
import com.example.httpserver.common.handler.AndroidFileHandler;
import com.example.httpserver.common.handler.AuthHandler;
import com.example.httpserver.common.handler.StaticFileHandler;
import com.example.httpserver.common.route.Router;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TinyHttpServer {

    private HttpServer server;
    private final ServiceConfigurationRepository repository;
    private AuthHandler authHandler;
    private AndroidFileHandler handler;
    private StaticFileHandler staticFileHandler;

    public static final String KEY_HTTP_PORT = "http_port";
    public static final String KEY_HTTP_TIMEOUT = "http_timeout";

    public static final String DEFAULT_HTTP_PORT = "4443";

    private final BiConsumer<Integer, Exception> DEFAULT_LISTENER = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {

        }
    };
    private BiConsumer<Integer, Exception> listener = DEFAULT_LISTENER;


    public TinyHttpServer(ServiceConfigurationRepository repository) {

        this.repository = repository;
    }

    private void init() {

        int port = Integer.parseInt(repository.get(KEY_HTTP_PORT, DEFAULT_HTTP_PORT));
        server = new HttpServer(port);
        server.router
                .add(Router.Route.of("GET", "/api/time", (session, map) -> NanoHTTPD.newFixedLengthResponse(new Date().toString())))

                .add(Router.Route.of("POST", "/web-api/login", authHandler::login))
                .add(Router.Route.of("POST", "/web-api/logout", authHandler::logout))
                .add(Router.Route.of("POST", "/web-api/refresh", authHandler::refresh))

                .add(Router.Route.of("GET", "/fs-api", handler::root))
                .add(Router.Route.of("GET", "/fs-api/{context}/{*path}", handler::get))
                .add(Router.Route.of("COPY", "/fs-api/{context}/{*path}", handler::copy))
                .add(Router.Route.of("MOVE", "/fs-api/{context}/{*path}", handler::move))
                .add(Router.Route.of("PUT", "/fs-api/{context}/{*path}", handler::put))
                .add(Router.Route.of("DELETE", "/fs-api/{context}/{*path}", handler::delete))

                .add(Router.Route.of("GET", "/", staticFileHandler::index))
                .add(Router.Route.of("GET", "/web/{*path}", staticFileHandler::file));

    }

    synchronized public void start() {
        if(server.isAlive()) {
            server.stop();
        }
        init();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            // todo handle exception
        }
    }


    synchronized public void stop() {
        if(server != null) {
            server.stop();
        }
        server = null;
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
                if(route == null) {
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
