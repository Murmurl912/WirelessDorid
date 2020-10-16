package com.example.httpserver.service.impl;

import com.example.httpserver.service.ContextService;
import com.example.httpserver.service.ContextServiceException;
import com.example.httpserver.service.FileService;
import com.example.httpserver.service.FileServiceException;
import fi.iki.elonen.NanoHTTPD;

import javax.security.auth.Subject;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;

public abstract class AbstractHttpFileHandlerProxy extends HttpFileHandler {

    public AbstractHttpFileHandlerProxy(FileService fileService, ContextService contextService) {
        super(fileService, contextService);
    }

    @Override
    public NanoHTTPD.Response get(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        try {
            return super.get(session, vars);
        } catch (ContextServiceException e) {
            return context(e);
        } catch (FileServiceException e) {
            return file(e);
        } catch (Exception e) {
            return error(e);
        }

    }

    @Override
    public NanoHTTPD.Response put(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        try {
            return super.put(session, vars);
        } catch (ContextServiceException e) {
            return context(e);
        } catch (FileServiceException e) {
            return file(e);
        } catch (Exception e) {
            return error(e);
        }

    }

    @Override
    public NanoHTTPD.Response move(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        try {
            return super.move(session, vars);
        } catch (ContextServiceException e) {
            return context(e);
        } catch (FileServiceException e) {
            return file(e);
        } catch (Exception e) {
            return error(e);
        }
    }

    @Override
    public NanoHTTPD.Response copy(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        try {
            return super.copy(session, vars);
        } catch (ContextServiceException e) {
            return context(e);
        } catch (FileServiceException e) {
            return file(e);
        } catch (Exception e) {
            return error(e);
        }
    }

    @Override
    public NanoHTTPD.Response delete(NanoHTTPD.IHTTPSession session, Map<String, String> vars) {
        try {
            return super.delete(session, vars);
        } catch (ContextServiceException e) {
            return context(e);
        } catch (FileServiceException e) {
            return file(e);
        } catch (Exception e) {
            return error(e);
        }
    }

    protected abstract NanoHTTPD.Response context(ContextServiceException e);

    protected abstract NanoHTTPD.Response file(FileServiceException e);

    protected abstract NanoHTTPD.Response error(Exception e);

}
