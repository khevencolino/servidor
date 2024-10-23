package com.kheven.handler;

import com.kheven.model.Request;
import com.kheven.http.Response;

@FunctionalInterface
public interface RouteHandler {
    void handle(Request request, Response response) throws Exception;
}
