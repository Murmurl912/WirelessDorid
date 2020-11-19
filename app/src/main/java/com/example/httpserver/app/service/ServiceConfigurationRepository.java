package com.example.httpserver.app.service;

public interface ServiceConfigurationRepository {
    public String get(String key);

    public String put(String key, String value);

    public String get(String key, String empty);

    public boolean contains(String key);

}
