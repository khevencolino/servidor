package com.kheven.http;

import com.kheven.status.ServerStatus;
import com.kheven.config.Config;
import com.kheven.handler.HttpHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A classe HttpServer é responsável por iniciar e gerenciar o servidor HTTP.
 */
public class HttpServer {
    private final int port;
    private final ExecutorService executor;
    private final Router router;
    private final ScheduledExecutorService statusReporter;

    /**
     * Construtor da classe HttpServer.
     * Inicializa a porta, o executor, o roteador e o statusReporter.
     */
    public HttpServer() {
        this.port = Config.getPort();
        // this.executor = Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory());
        // this.executor = Executors.newCachedThreadPool();
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.router = new Router();
        this.statusReporter = Executors.newSingleThreadScheduledExecutor();
        setupRoutes();
    }

    /**
     * Configura as rotas do servidor.
     */
    private void setupRoutes() {
        router.addRoute("GET", "/", (req, res) -> {
            res.setStatusCode(200);
            res.setBody("Bem-vindo ao servidor HTTP!");
        });

        // Adiciona uma rota com atraso de 2 segundos
        router.addRoute("GET", "/slow", (req, res) -> {
            // Cria um atraso de 2 segundos para simular um processamento lento
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
            res.setStatusCode(200);
            res.setBody("Slow response");
        });

        // Adiciona uma rota para simular uma resposta JSON
        router.addRoute("GET", "/api/users", (req, res) -> {
            res.setStatusCode(200);
            res.setContentType("application/json");
            res.setBody("[{\"id\": 1, \"name\": \"John Doe\"}, {\"id\": 2, \"name\": \"Jane Doe\"}]");
        });

        // Adiciona uma rota para simular um erro 404
        router.addRoute("GET", "/api/404", (req, res) -> {
            res.setStatusCode(404);
            res.setBody("Not Found");
        });

        // Adicona uma rota POST
        router.addRoute("POST", "/api/users", (req, res) -> {
            res.setStatusCode(200);
            res.setContentType("application/json");
            res.setBody("{\"message\": \"User created\"}");
        });

        // Adiciona uma rota PUT
        router.addRoute("PUT", "/api/users/1", (req, res) -> {
            res.setStatusCode(200);
            res.setContentType("application/json");
            res.setBody("{\"message\": \"User updated\"}");
        });

        // Adiciona uma rota DELETE
        router.addRoute("DELETE", "/api/users/1", (req, res) -> {
            res.setStatusCode(200);
            res.setContentType("application/json");
            res.setBody("{\"message\": \"User deleted\"}");
        });
    }

    /**
     * Inicia o servidor HTTP.
     *
     * @throws IOException se ocorrer um erro de I/O.
     */
    public void start() throws IOException {
        startStatusReporter();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Exibe a mensagem de que o servidor foi iniciado
            System.out.println("Servidor iniciado na porta: " + port);
            // Aguarda a conexão de clientes
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Atualiza o status do servidor
                ServerStatus.clientConnected();
                ServerStatus.requestReceived();
                ServerStatus.threadCreated();
                // Inicia uma nova thread para lidar com o cliente
                executor.submit(() -> handleClient(clientSocket));
            }
        } finally {
            stopStatusReporter();
            executor.shutdown();
            // Aguarda a finalização de todas as threads
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * Lida com a conexão do cliente.
     *
     * @param clientSocket o socket do cliente.
     */
    private void handleClient(Socket clientSocket) {
        // Atualiza o status do servidor e cria um novo manipulador HTTP
        try (clientSocket) {
            HttpHandler handler = new HttpHandler(clientSocket, router);
            handler.handle();
            // Relata o status do servidor a cada requisição, ( LENTO)
            // clearConsole();
            // System.out.println(ServerStatus.getStatusReport());
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            // Atualiza o status do servidor e destrói a thread
            ServerStatus.clientDisconnected();
            ServerStatus.threadDestroyed();
        }
    }

    /**
     * Inicia o statusReporter para relatar o status do servidor periodicamente.
     */
    private void startStatusReporter() {
        // Relata o status do servidor a cada segundo
        statusReporter.scheduleAtFixedRate(
                () -> { clearConsole(); System.out.flush(); System.out.print(ServerStatus.getStatusReport()); },
                0, 1, TimeUnit.SECONDS
        );
    }

    /**
     * Para o statusReporter.
     */
    private void stopStatusReporter() {
        statusReporter.shutdownNow();
        try {
            statusReporter.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Status reporter shutdown interrupted");
        }
    }

    /**
     * Limpa o console.
     */
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal para iniciar o servidor.
     *
     * @param args argumentos da linha de comando.
     */
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}