package com.example.httpserver.app.services.http.server;

import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.http.route.Router;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TinyWebServer implements WebServer {

    private ServerConfig config;
    private HttpServer server;
    private final static int DEFAULT_PORT = 8080;
    private final static int PORT_LOW = 1;
    private final static int PORT_HIGH = 65535;
    private final static int DEFAULT_TIMEOUT = 4000;
    private volatile int state;

    private static final int STATE_STOPPED = 0;
    private static final int STATE_STARTING = 1;
    private static final int STATE_RUNNING = 2;
    private static final int STATE_STOPPING = 3;
    private static final int STATE_ERROR = -1;
    private Exception error;

    private final BiConsumer<Integer, Exception> DEFAULT_LISTENER = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {

        }
    };
    private BiConsumer<Integer, Exception> listener = DEFAULT_LISTENER;

    private final Router<BiFunction<NanoHTTPD.IHTTPSession, Map<String, String>, NanoHTTPD.Response>> router;

    public TinyWebServer() {
        this.router = new Router<>();
    }

    public void setConfig(ServerConfig config) {
        this.config = config;
    }

    private void init() {
        if(server != null) {
            server.stop();
        }
        server = new HttpServer(8080);
    }

    @Override
    synchronized public void start() {
        switch (state) {
            case STATE_RUNNING:
                stop();
                init();
                break;
            case STATE_STOPPED:
                init();
                break;
            default:
                throw new IllegalStateException();
        }
        state = STATE_STARTING;
        listener.accept(state, null);
        try {
            server.start(DEFAULT_TIMEOUT);
            state = STATE_RUNNING;
            listener.accept(state, null);
        } catch (IOException e) {
            error = e;
            state = STATE_ERROR;
            listener.accept(state, e);
        }
    }


    @Override
    synchronized public void stop() {
        switch (state) {
            case STATE_RUNNING:
            case STATE_STOPPED:
                break;
            default:
                throw new IllegalStateException();
        }
        state = STATE_STOPPING;
        listener.accept(state, null);
        try {
            server.stop();
            state = STATE_STOPPED;
            listener.accept(state, null);
        } catch (Exception e) {
            error = e;
            state = STATE_ERROR;
            listener.accept(state, null);
        }
    }


    public Router<BiFunction<NanoHTTPD.IHTTPSession, Map<String, String>, NanoHTTPD.Response>> router() {
        return router;
    }

    private class HttpServer extends NanoHTTPD {

        private final Router<BiFunction<NanoHTTPD.IHTTPSession, Map<String, String>, NanoHTTPD.Response>> router = TinyWebServer.this.router;

        public HttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            String method = session.getMethod().toString();

            HashMap<String, String> pathVariables = new HashMap<>();
            Router.Route<BiFunction<IHTTPSession, Map<String, String>, Response>> route = router.route(method, uri, pathVariables);
            if(route == null) {
                return super.serve(session);
            }
            return route.handler.apply(session, pathVariables);
        }

    }

    public void setServerListener(BiConsumer<Integer, Exception> listener) {
        this.listener = listener == null ? DEFAULT_LISTENER : listener;
    }

    public void removeServerListener() {
        this.listener = DEFAULT_LISTENER;
    }

    public static NanoHTTPD.Response response(String message) {
        return NanoHTTPD.newFixedLengthResponse(message);
    }

}
