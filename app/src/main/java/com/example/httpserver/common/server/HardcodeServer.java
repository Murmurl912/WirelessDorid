package com.example.httpserver.common.server;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.util.concurrent.locks.ReentrantLock;

public class HardcodeServer {
    private AsyncHttpServer server;
    private int port;
    private volatile int state = 3;
    private ReentrantLock lock;

    private final CompletedCallback callback = ex -> {
        state = 4;
    };

    public HardcodeServer() {
        lock = new ReentrantLock();
        server = new AsyncHttpServer();
        server.setErrorCallback(callback);
    }

    private void startWithState() {
        lock.lock();
        try {
            state = 0; // starting
            server.listen(port);
            state = 1; // running
        } finally {
            lock.unlock();
        }
    }

    private void stopWithState() {
        lock.lock();
        try {
            state = 2; // stopping
            server.stop();
            state = 3; // stopped
        } finally {
            lock.unlock();
        }
    }

    public void start() {
        switch (state) {
            case 0:
            case 1:
                break;
            case 2:
            case 3:
                stopWithState();
                startWithState();
                break;
            default:
        }
    }

    public void stop() {
        switch (state) {
            case 0: // server is starting
            case 1: // server is running
                stopWithState();
                break;
        }
    }

    private class WebFSHandler implements HttpServerRequestCallback {
        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

        }
    }

    private void get() {

    }

    private void delete() {

    }

    private void post() {

    }

    private void patch() {

    }

    private void put() {

    }

    private void options() {

    }

    private void trace() {

    }

}
