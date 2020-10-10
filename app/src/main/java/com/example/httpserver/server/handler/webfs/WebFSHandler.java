package com.example.httpserver.server.handler.webfs;

import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

public class WebFSHandler implements HttpServerRequestCallback {

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String url = request.getUrl();
        response.code(200).send("url: " + url);
    }

}
