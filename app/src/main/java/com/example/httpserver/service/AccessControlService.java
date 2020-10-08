package com.example.httpserver.service;

public interface AccessControlService<Resource, Action, Principle> {

    public Principle principle();

    public boolean access(Resource resource, Action action);

}
