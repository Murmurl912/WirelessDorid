package com.example.httpserver.app.repository.entity;

public interface KeyValuePair<Key, Value> {
    public Key key();

    public Value value();

    public void key(Key key);

    public void value(Value value);

}
