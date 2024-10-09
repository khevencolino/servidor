package com.kheven.http;

import java.util.Map;

/**
 * A classe Request representa uma requisição HTTP com método, caminho, cabeçalhos e corpo.
 */
public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    /**
     * Construtor da classe Request.
     *
     * @param method o método HTTP da requisição (e.g., GET, POST).
     * @param path o caminho da requisição.
     * @param headers os cabeçalhos da requisição.
     * @param body o corpo da requisição.
     */
    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Obtém o método HTTP da requisição.
     *
     * @return o método HTTP.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Obtém o caminho da requisição.
     *
     * @return o caminho da requisição.
     */
    public String getPath() {
        return path;
    }

    /**
     * Obtém os cabeçalhos da requisição.
     *
     * @return um mapa contendo os cabeçalhos da requisição.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Obtém o corpo da requisição.
     *
     * @return o corpo da requisição.
     */
    public String getBody() {
        return body;
    }
}