package com.example.httpserver.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerManager {

    public static final int STATUS_START = 1;
    public static final int STATUS_STOP = 2;
    public static final int STATUS_STARING = 3;
    public static final int STATUS_STOPPING = 4;
    private volatile int status;

    private HttpServer server;
    private ExecutorService executor;
    private final Queue<HttpServerStatusListener> listeners;
    private final ConcurrentHashMap<String, HttpHandler> handlers;

    private final HttpHandler DEFAULT_HANDLER = exchange -> {
        exchange.sendResponseHeaders(200, 0);
    };

    public HttpServerManager() {
        listeners = new ConcurrentLinkedQueue<>();
        handlers = new ConcurrentHashMap<>();
        init();
    }

    private void init() {
        if(executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }
    }

    public HttpServerStatusListener addListener(HttpServerStatusListener listener) {
        if(listener != null)
            listeners.add(listener);
        return listener;
    }

    public void removeListener(HttpServerStatusListener listener) {
        listeners.remove(listener);
    }

    public void notifyStatus(int serverStatus) {
        listeners.stream().parallel().forEach(listener -> {
            try {
                listener.onStatus(serverStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void notifyError(Exception e) {
        listeners.stream().parallel().forEach(listener -> {
            try {
                listener.onError(e);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void start() {
        if (status != STATUS_STOP) {
            return;
        }
        executor.submit(()->{
            status = STATUS_STARING;
            notifyStatus(status);
            try {
                server = HttpServer.create();
                server.bind(new InetSocketAddress(8080), 0);
                server.createContext("/**", DEFAULT_HANDLER);
                server.start();
                status = STATUS_START;
                notifyStatus(status);
            } catch (IOException e) {
                status = STATUS_STOP;
                notifyStatus(status);
                notifyError(e);
            }
        });
    }

    public void stop() {
        if(status != STATUS_START) {
            return;
        }
        executor.submit(()->{
            status = STATUS_STOPPING;
            notifyStatus(status);
            server.stop(5000);
            server = null;
            status = STATUS_STOP;
            notifyStatus(status);
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public static interface HttpServerStatusListener {
        public void onStatus(int status);

        public void onError(Exception e);
    }

}
