package com.kheven.handler;

import com.kheven.http.Response;
import com.kheven.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Essa classe é responsável por servir arquivos estáticos (HTML, CSS, JS) para o cliente. Falta testar.
public class StaticFileHandler {
    public static void serveStaticFile(String path, Response response) throws IOException {
        Path filePath = Paths.get(Config.getStaticDir(), path);
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            response.setStatusCode(200);
            response.setContentType(getContentType(filePath));
            response.setBody(Files.readString(filePath));
        } else {
            response.setStatusCode(404);
            response.setBody("404 Not Found");
        }
    }

    private static String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "text/plain";
        }
    }
}

