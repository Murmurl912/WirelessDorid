package com.example.httpserver.common.handler;

import com.example.httpserver.common.model.LoginModel;
import com.example.httpserver.common.service.AuthService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler {

    private final AuthService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuthHandler(AuthService service) {
        this.service = service;
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }

    public NanoHTTPD.Response login(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String contenttype = session.getHeaders().getOrDefault("content-type", "");
        contenttype = contenttype == null ? "" : contenttype;
        LoginModel model = new LoginModel();
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (IOException | NanoHTTPD.ResponseException e) {
            bad("Failed to parse body: " + e.toString());
        }

        switch (contenttype) {
            case "application/x-www-form-urlencoded":
                List<String> l = session.getParameters().get("username");
                if (l != null && !l.isEmpty()) {
                    model.username = l.get(0);
                }
                l = session.getParameters().get("password");
                if (l != null && !l.isEmpty()) {
                    model.password = l.get(0);
                }
                l = session.getParameters().get("pin");
                if (l != null && !l.isEmpty()) {
                    model.pin = l.get(0);
                }
                break;
            default:
                return bad("content type not supported");
        }

        try {
            String[] tokens = service.login(model);
            NanoHTTPD.Response response = ok(tokens);
            session.getCookies().set("refresh", tokens[1], 14);

            return response;
        } catch (Exception e) {
            return bad("Authentication failed due to error or invalid credentials");
        }
    }

    public NanoHTTPD.Response logout(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        session.getCookies().set("refresh", "", 0);
        return ok();
    }

    public NanoHTTPD.Response refresh(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        String auth = session.getHeaders().get("authorization");
        try {
            String token = service.refresh(auth);
            return ok(token);
        } catch (Exception e) {
            // try cookie
            String refresh = session.getCookies().read("refresh");
            try {
                String token = service.refresh(refresh);
                return ok(token);
            } catch (Exception x) {
                return bad("Token is invalid or not present or out date");
            }
        }
    }

    private NanoHTTPD.Response ok() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "");
    }

    private NanoHTTPD.Response ok(String token) {
        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", token);
        response.addHeader("authorization", token);
        return response;
    }

    private NanoHTTPD.Response ok(String[] token) {
        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", token[1]);
        response.addHeader("authorization", token[0]);
        return response;
    }

    private NanoHTTPD.Response bad() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "");
    }

    private NanoHTTPD.Response bad(String message) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", message);
    }
}
