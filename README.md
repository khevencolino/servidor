
# Servidor HTTP Simples

Este projeto é um servidor HTTP simples implementado em Java. Ele é capaz de lidar com requisições HTTP, servir arquivos estáticos e gerenciar rotas dinâmicas.


### Tecnologias Utilizadas
- **Java**: Linguagem de programação utilizada para implementar o servidor. (Versão minima: 17; Versão utilizada: 21)
- **Maven**: Ferramenta de automação de compilação e gerenciamento de dependências.
- **ExecutorService**: Utilizado para gerenciar threads.
- **ServerSocket** e **Socket**: Utilizados para gerenciar conexões de rede.
- **Postman**: Ferramenta para testar requisições HTTP.

### Tipo do Socket
- **ServerSocket**: Utilizado para aceitar conexões de clientes.
- **Socket**: Representa a conexão com o cliente.

### Tipo da Thread
- **Virtual Threads**: Utilizadas para gerenciar as conexões de clientes de forma eficiente, permitindo a criação de muitas threads leves.

### Requisitos do Protocolo HTTP Implementados:
- **Métodos HTTP**: Suporte para métodos GET, POST, PUT, DELETE.
- **Status Codes**: Implementação de códigos de status como 200 (OK), 404 (Not Found), 405 (Method Not Allowed), 500 (Internal Server Error).
- **Headers**: Manipulação de cabeçalhos HTTP.
- **Content-Type**: Suporte para diferentes tipos de conteúdo (e.g., text/plain, application/json).

### Requisitos do Protocolo HTTP não Implementados:
- **HTTPS**: Não há suporte para conexões seguras (SSL/TLS).
- **HTTP/2**: Não há suporte para a versão 2 do protocolo HTTP.
- **Autenticação**: Não há suporte para mecanismos de autenticação HTTP.
- **Cookies**: Não há manipulação de cookies.
- **Redirecionamentos**: Não há suporte para redirecionamentos HTTP.
- **Compressão**: Não há suporte para compressão de respostas (e.g., gzip).
## Estrutura do Projeto

```
server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── kheven/
│   │   │           ├── config/
│   │   │           │   └── Config.java
│   │   │           ├── handler/
│   │   │           │   ├── HttpHandler.java
│   │   │           │   ├── RouteHandler.java
│   │   │           │   └── StaticFileHandler.java
│   │   │           ├── http/
│   │   │           │   ├── HttpServer.java
│   │   │           │   ├── Request.java
│   │   │           │   ├── Response.java
│   │   │           │   └── Router.java
│   │   │           ├── model/
│   │   │           │   └── Route.java
│   │   │           └── status/
│   │   │               └── ServerStatus.java
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── kheven/
│       │           └── (test files)
│       └── resources/
├── pom.xml
└── README.md
```

## Fluxo do Programa

1. **Inicialização do Servidor**: O ponto de entrada do programa é o método `main` na classe `HttpServer`. Ele inicializa o servidor HTTP, configurando a porta e as rotas.

    ```java
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    ```

2. **Configuração das Rotas**: As rotas são configuradas no método `setupRoutes` da classe `HttpServer`. Cada rota é associada a um manipulador (`RouteHandler`) que define a lógica para lidar com a requisição.

    ```java
    private void setupRoutes() {
        router.addRoute("GET", "/", (req, res) -> {
            res.setStatusCode(200);
            res.setBody("Bem-vindo ao servidor HTTP!");
        });
        // Outras rotas...
    }
    ```

3. **Gerenciamento de Conexões**: O servidor aceita conexões de clientes através de `ServerSocket` e cria uma nova thread para cada conexão usando um `ExecutorService`.

    ```java
    try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor iniciado na porta: " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ServerStatus.clientConnected();
            ServerStatus.requestReceived();
            executor.submit(() -> handleClient(clientSocket));
        }
    }
    ```

4. **Manipulação de Requisições**: A classe `HttpHandler` é responsável por processar as requisições HTTP. Ela lê a requisição, encontra a rota correspondente e gera a resposta.

    ```java
    public void handle() throws IOException {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            Request request = parseRequest(in);
            Response response = new Response();
            try {
                Route route = router.findRoute(request.getMethod(), request.getPath());
                if (route != null) {
                    route.getHandler().handle(request, response);
                } else if (request.getMethod().equals("GET")) {
                    StaticFileHandler.serveStaticFile(request.getPath(), response);
                } else {
                    response.setStatusCode(405);
                    response.setBody("Method Not Allowed");
                }
            } catch (Exception e) {
                response.setStatusCode(500);
                response.setBody("Internal Server Error");
                System.err.println("Error processing request: " + e.getMessage());
            }
            sendResponse(out, response);
        }
    }
    ```

## Gerenciamento de Threads e Sockets

- **ExecutorService**: O servidor utiliza um `ExecutorService` para gerenciar as threads. Cada conexão de cliente é tratada em uma nova thread, permitindo que o servidor lide com múltiplas conexões simultaneamente.

    ```java
    this.executor = Executors.newVirtualThreadPerTaskExecutor();
    ```

- **ServerSocket**: A classe `ServerSocket` é usada para aceitar conexões de clientes. Cada vez que um cliente se conecta, um novo `Socket` é criado para essa conexão.

    ```java
    try (ServerSocket serverSocket = new ServerSocket(port)) {
        // Aceita conexões de clientes
    }
    ```

- **Socket**: A classe `Socket` representa a conexão com o cliente. O `HttpHandler` utiliza o `Socket` para ler a requisição e enviar a resposta.

    ```java
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         OutputStream out = clientSocket.getOutputStream()) {
        // Manipula a requisição e resposta
    }
    ```

## Configuração

As configurações do servidor, como a porta e o diretório de arquivos estáticos, são carregadas a partir do arquivo `config.properties` pela classe `Config`.

```java
public class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a configuração: " + e.getMessage());
        }
    }

    public static int getPort() {
        return Integer.parseInt(properties.getProperty("server.port", "8080"));
    }

    public static String getStaticDir() {
        return properties.getProperty("server.static.dir", "static");
    }
}
```

## Monitoramento do Servidor

A classe `ServerStatus` monitora e reporta o status do servidor, incluindo o número de clientes conectados, requisições recebidas, threads ativas e uso de memória e CPU.

```java
public class ServerStatus {
    private static final AtomicInteger connectedClients = new AtomicInteger(0);
    private static final AtomicInteger requestsReceived = new AtomicInteger(0);
    private static final AtomicInteger currentThreads = new AtomicInteger(0);

    public static void clientConnected() {
        connectedClients.incrementAndGet();
    }

    public static void requestReceived() {
        requestsReceived.incrementAndGet();
    }

    public static void threadCreated() {
        currentThreads.incrementAndGet();
    }

    public static void threadDestroyed() {
        currentThreads.decrementAndGet();
    }

    public static void clientDisconnected() {
        connectedClients.decrementAndGet();
    }

    public static String getStatusReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== Status do Servidor ===\n");
        report.append("Clientes Conectados: ").append(connectedClients.get()).append("\n");
        report.append("Requisições Recebidas: ").append(requestsReceived.get()).append("\n");
        report.append("Threads Ativas (PLATAFORMA): ").append(Thread.activeCount()).append("\n");
        report.append("Threads Atuais: ").append(currentThreads.get()).append("\n");
        report.append("Uso de Memória: ").append(getMemoryUsage()).append("\n");
        report.append("Carga de CPU: ").append(getCpuLoad()).append("\n");
        report.append("=====================\n");
        return report.toString();
    }

    private static String getMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return String.format("%.2f MB / %.2f MB", usedMemory / (1024.0 * 1024.0), totalMemory / (1024.0 * 1024.0));
    }

    private static String getCpuLoad() {
        if (osMBean instanceof com.sun.management.OperatingSystemMXBean) {
            double processCpuLoad = ((com.sun.management.OperatingSystemMXBean) osMBean).getProcessCpuLoad() * 100;
            return processCpuLoad < 0 ? "N/A" : String.format("%.2f%%", processCpuLoad);
        }
        return "N/A";
    }
}
```

## Como Executar

1. **Clone o repositório**:
    ```sh
    git clone <URL_DO_REPOSITORIO>
    cd my-http-server
    ```

2. **Compile o projeto**:
    ```sh
    mvn clean install
    ```

3. **Execute o servidor**:
    ```sh
    mvn exec:java -Dexec.mainClass="com.kheven.http.HttpServer"
    ```

O servidor estará disponível na porta configurada (por padrão, 8080).

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo `LICENSE` para obter mais informações.
