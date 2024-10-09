package com.kheven.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A classe Config é responsável por carregar e fornecer configurações
 * a partir de um arquivo de propriedades.
 */
public class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties;

    // Bloco estático para carregar as propriedades do arquivo de configuração
    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a configuração: " + e.getMessage());
        }
    }

    /**
     * Obtém a porta do servidor a partir das propriedades.
     *
     * @return a porta do servidor, ou 8080 se não estiver definida.
     */
    public static int getPort() {
        return Integer.parseInt(properties.getProperty("server.port", "8080"));
    }

    /**
     * Obtém o diretório estático do servidor a partir das propriedades.
     *
     * @return o diretório estático do servidor, ou "static" se não estiver definido.
     */
    public static String getStaticDir() {
        return properties.getProperty("server.static.dir", "static");
    }
}