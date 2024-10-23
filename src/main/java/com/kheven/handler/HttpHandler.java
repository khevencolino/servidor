package com.kheven.handler;

import com.kheven.model.Request;
import com.kheven.http.Response;
import com.kheven.model.Route;
import com.kheven.http.Router;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe HttpHandler é responsável por lidar com as requisições HTTP
 * recebidas pelo servidor.
 */
public class HttpHandler {
    private final Socket clientSocket;
    private final Router router;

    /**
     * Construtor da classe HttpHandler.
     *
     * @param clientSocket o socket do cliente.
     * @param router o roteador para encontrar as rotas.
     */
    public HttpHandler(Socket clientSocket, Router router) {
        this.clientSocket = clientSocket;
        this.router = router;
    }

    /**
     * Método principal para lidar com a requisição HTTP.
     *
     * @throws IOException se ocorrer um erro de I/O.
     */
    public void handle() throws IOException {

        // Tenta abrir os streams de entrada e saída do socket do cliente
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            // Analisa a requisição HTTP recebida
            Request request = parseRequest(in);
            Response response = new Response();

            // Tenta encontrar a rota correspondente à requisição
            try {
                Route route = router.findRoute(request.getMethod(), request.getPath());
                if (route != null) {
                    // Se a rota for encontrada, chama o handler da rota
                    route.getHandler().handle(request, response);
                } else if (request.getMethod().equals("GET")) {
                    // Se a rota não for encontrada e o método for GET, tenta servir um arquivo estático
                    StaticFileHandler.serveStaticFile(request.getPath(), response);
                } else {
                    // Se o método não for permitido, retorna o status 405
                    response.setStatusCode(405);
                    response.setBody("Method Not Allowed");
                }
            } catch (Exception e) {
                // Em caso de erro, retorna o status 500
                response.setStatusCode(500);
                response.setBody("Internal Server Error");
                System.err.println("Error processing request: " + e.getMessage());
            }

            // Envia a resposta HTTP para o cliente
            sendResponse(out, response);
        } finally {
            // Fecha o socket do cliente
            clientSocket.close();
        }
    }

    /**
     * Analisa a requisição HTTP a partir do BufferedReader.
     *
     * @param in o BufferedReader para ler a requisição.
     * @return um objeto Request representando a requisição.
     * @throws IOException se ocorrer um erro de I/O.
     */
    private Request parseRequest(BufferedReader in) throws IOException {
        // Lê a primeira linha da requisição (linha de status)
        String firstLine = in.readLine();
        // Divide a linha de status em partes (método HTTP e caminho)
        String[] parts = firstLine.split(" ");
        String method = parts[0];
        String path = parts[1];

        // Inicializa um mapa para armazenar os cabeçalhos da requisição
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        // Lê os cabeçalhos da requisição até encontrar uma linha vazia
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            // Divide cada linha de cabeçalho em chave e valor
            String[] headerParts = headerLine.split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        // Inicializa um StringBuilder para armazenar o corpo da requisição
        StringBuilder body = new StringBuilder();
        // Se o cabeçalho "Content-Length" estiver presente, lê o corpo da requisição
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        // Retorna um novo objeto Request com o método, caminho, cabeçalhos e corpo
        return new Request(method, path, headers, body.toString());
    }

    /**
     * Envia a resposta HTTP para o cliente.
     *
     * @param out o OutputStream para enviar a resposta.
     * @param response a resposta HTTP.
     * @throws IOException se ocorrer um erro de I/O.
     */
    private void sendResponse(OutputStream out, Response response) throws IOException {
        out.write(("HTTP/1.1 " + response.getStatusCode() + " " + getStatusMessage(response.getStatusCode()) + "\r\n").getBytes());
        out.write(("Content-Type: " + response.getContentType() + "\r\n").getBytes());
        out.write(("Content-Length: " + response.getBody().length() + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(response.getBody().getBytes());
        out.flush();
    }

    /**
     * Obtém a mensagem de status HTTP correspondente ao código de status.
     *
     * @param statusCode o código de status HTTP.
     * @return a mensagem de status correspondente.
     */
    private String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "Unknown Status";
        };
    }
}