package com.example.httpserver.app.services.http.server;

import android.util.Log;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.http.route.Router;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.security.cert.CertificateException;
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

    public static final int STATE_STOPPED = 0;
    public static final int STATE_STARTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_STOPPING = 3;
    public static final int STATE_ERROR = -1;
    public Exception error;

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
        server = new HttpServer(config.http_port);
//        if(config.tls) {
//            try {
//                SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
//                server.makeSecure(factory, new String[]{"tls"});
//            } catch (Exception e) {
//                App.app().db().configuration().save(new Configuration("tls", "false"));
//                config.tls = false;
//            }
//        }

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
                throw new IllegalStateException("Server should not in starting or stopping state when stop is invoked");
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
                throw new IllegalStateException("Server should not in starting or stopping state when stop is invoked");
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

        private ObjectMapper mapper = new ObjectMapper();

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
            try {
                return route.handler.apply(session, pathVariables);
            } catch (Exception e) {
                listener.accept(state, e);
                return error(e);
            }
        }

        private NanoHTTPD.Response error(Exception e) {
            String json = e.getMessage();

            try {
                json = mapper.writeValueAsString(e);
            } catch (Exception ignored) {

            }
            String code = "<code>" + json + "<code>";
            return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                    "text/html", "<h>Internal Server Error</h>" + code);
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

    public static class ResponseBuilder {

    }
}
