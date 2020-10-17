package com.example.httpserver.common.repository;

public interface ServiceConfigRepository {

    public String get(String key);

    public void put(String key, String value);

}
