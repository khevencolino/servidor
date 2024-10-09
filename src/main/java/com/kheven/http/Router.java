package com.kheven.http;

import com.kheven.model.Route;
import com.kheven.handler.RouteHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A classe Router é responsável por gerenciar as rotas do servidor HTTP.
 */
public class Router {
    private final List<Route> routes = new ArrayList<>();

    /**
     * Adiciona uma nova rota ao roteador.
     *
     * @param method o método HTTP (e.g., GET, POST).
     * @param path o caminho da rota.
     * @param handler o manipulador da rota.
     */
    public void addRoute(String method, String path, RouteHandler handler) {
        routes.add(new Route(method, path, handler));
    }

    /**
     * Encontra uma rota correspondente ao método e caminho fornecidos.
     *
     * @param method o método HTTP da requisição.
     * @param path o caminho da requisição.
     * @return a rota correspondente, ou null se não for encontrada.
     */
    public Route findRoute(String method, String path) {
        return routes.stream()
                .filter(route -> route.matches(method, path))
                .findFirst()
                .orElse(null);
    }
}