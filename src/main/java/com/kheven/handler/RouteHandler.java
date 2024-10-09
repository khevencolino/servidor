package com.kheven.handler;

import com.kheven.http.Request;
import com.kheven.http.Response;

@FunctionalInterface
public interface RouteHandler {
    void handle(Request request, Response response) throws Exception;
}
