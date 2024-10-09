package com.kheven.http;

/**
 * A classe Response representa uma resposta HTTP com código de status,
 * tipo de conteúdo e corpo da resposta.
 */
public class Response {
    private int statusCode;
    private String contentType;
    private String body;

    /**
     * Construtor padrão da classe Response.
     * Inicializa a resposta com código de status 200, tipo de conteúdo "text/plain" e corpo vazio.
     */
    public Response() {
        this.statusCode = 200;
        this.contentType = "text/plain";
        this.body = "";
    }

    /**
     * Obtém o código de status da resposta.
     *
     * @return o código de status.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Define o código de status da resposta.
     *
     * @param statusCode o novo código de status.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Obtém o tipo de conteúdo da resposta.
     *
     * @return o tipo de conteúdo.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Define o tipo de conteúdo da resposta.
     *
     * @param contentType o novo tipo de conteúdo.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Obtém o corpo da resposta.
     *
     * @return o corpo da resposta.
     */
    public String getBody() {
        return body;
    }

    /**
     * Define o corpo da resposta.
     *
     * @param body o novo corpo da resposta.
     */
    public void setBody(String body) {
        this.body = body;
    }
}