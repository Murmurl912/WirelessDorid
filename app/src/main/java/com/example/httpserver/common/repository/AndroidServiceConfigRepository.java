package com.example.httpserver.common.repository;

import com.example.httpserver.app.repository.ConfigurationRepository;

public class AndroidServiceConfigRepository implements ServiceConfigRepository {

    private final ConfigurationRepository repository;

    public AndroidServiceConfigRepository(ConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String get(String key) {
        return repository.get(key);
    }

    @Override
    public void put(String key, String value) {
        repository.put(key, value);
    }
}
