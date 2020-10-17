package com.example.httpserver.common.handler;

import com.example.httpserver.common.model.LoginModel;
import com.example.httpserver.common.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import fi.iki.elonen.NanoHTTPD;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuthHandler {

    private final AuthService service;

    public AuthHandler(AuthService service) {
        this.service = service;
    }

    public void login(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        Map<String, List<String>> params = session.getParameters();

        if(params.containsKey("login")) {
            List<String> list = params.get("data");
            if(list != null && !list.isEmpty()) {
                login(list.get(0));
            }
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(session.getInputStream()));
        login(reader.lines().collect(Collectors.joining()));
    }

    public NanoHTTPD.Response logout(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        session.getCookies().set("refresh", "", 0);
        return NanoHTTPD.newFixedLengthResponse("");
    }

    private NanoHTTPD.Response login(String data) {
        try {
            return ok(service.login(data));
        } catch (Exception e) {
            return bad();
        }
    }

    private NanoHTTPD.Response ok(String tokens) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", tokens);
    }

    private NanoHTTPD.Response bad() {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "");
    }

}
