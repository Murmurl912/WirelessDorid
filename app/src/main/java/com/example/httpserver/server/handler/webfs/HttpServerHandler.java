package com.example.httpserver.server.handler.webfs;

import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

public interface HttpServerHandler extends HttpServerRequestCallback {
    @Override
    default void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        try {
            handle(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
