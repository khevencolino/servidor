package com.kheven.model;

import com.kheven.handler.RouteHandler;

/**
 * A classe Route representa uma rota HTTP com método, caminho e manipulador.
 */
public class Route {
    private final String method;
    private final String path;
    private final RouteHandler handler;

    /**
     * Construtor da classe Route.
     *
     * @param method o método HTTP (e.g., GET, POST).
     * @param path o caminho da rota.
     * @param handler o manipulador da rota.
     */
    public Route(String method, String path, RouteHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    /**
     * Verifica se a rota corresponde ao método e caminho fornecidos.
     *
     * @param method o método HTTP da requisição.
     * @param path o caminho da requisição.
     * @return true se a rota corresponder, false caso contrário.
     */
    public boolean matches(String method, String path) {
        return this.method.equals(method) && this.path.equals(path);
    }

    /**
     * Obtém o manipulador da rota.
     *
     * @return o manipulador da rota.
     */
    public RouteHandler getHandler() {
        return handler;
    }
}