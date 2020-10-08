package com.example.httpserver.service;

import java.util.Map;

public interface AuthenticationService {

    public boolean authenticate(Map<String, ?> info);

}
