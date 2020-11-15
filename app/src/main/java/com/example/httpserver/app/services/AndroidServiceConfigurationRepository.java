package com.example.httpserver.app.services;

import com.example.httpserver.app.repository.ConfigurationRepository;

public class AndroidServiceConfigurationRepository implements ServiceConfigurationRepository {
    private final ConfigurationRepository repository;

    public AndroidServiceConfigurationRepository(ConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String get(String key) {
        return repository.get(key);
    }

    @Override
    public String put(String key, String value) {
        repository.put(key, value);
        return repository.get(key);
    }

    @Override
    public String get(String key, String empty) {
        String value = repository.get(key);
        if (value == null) {
            repository.put(key, empty);
        }
        return repository.get(key);
    }

    @Override
    public boolean contains(String key) {
        return repository.contains(key) > 0;
    }
}
