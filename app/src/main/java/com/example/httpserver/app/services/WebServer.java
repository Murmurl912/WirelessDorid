package com.example.httpserver.app.services;

import android.net.Uri;
import com.example.httpserver.R;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebServer extends NanoHTTPD {

    private final ConcurrentHashMap<String, Map<String, ?>> sessionStore = new ConcurrentHashMap<>();

    public WebServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        boolean result = filter(session);
        if(!result) {
            return newFixedLengthResponse(Response.Status.NO_CONTENT, "text/plain", null);
        }
        return dispatch(session);
    }

    private Response dispatch(IHTTPSession session) {
        switch (session.getMethod()) {
            case GET:
                return get(session);
            case PUT:
                return put(session);
            case PATCH:
                return patch(session);
            case POST:
                return post(session);
            case DELETE:
                return delete(session);
            default:
                return super.serve(session);
        }
    }

    private boolean filter(IHTTPSession session) {
        String id = session.getCookies().read("sessionid");
        if(id == null || id.equals("")) {
            Cookie cookie = new Cookie("sessionid", UUID.randomUUID().toString(), "session");
            session.getCookies().set(cookie);
        }
        return true;
    }

    public Response get(IHTTPSession session) {
        String uri = session.getUri();
        String method = session.getMethod().name();
        String parameters = session.getParameters().toString();
        String sessionid = session.getCookies().read("sessionid");
        Map<String, String> json = new HashMap<>();
        json.put("uri", uri);
        json.put("method", method);
        json.put("parameters", parameters);
        json.put("session", sessionid);

        return newFixedLengthResponse(Response.Status.OK, "application/json", json.toString());
    }

    public Response put(IHTTPSession session) {
        return get(session);
    }

    public Response post(IHTTPSession session) {
        return get(session);
    }

    public Response delete(IHTTPSession session) {
        return get(session);
    }

    public Response patch(IHTTPSession session) {
        return get(session);
    }

}