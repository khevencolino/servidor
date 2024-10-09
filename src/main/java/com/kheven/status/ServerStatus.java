package com.kheven.status;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicInteger;


public class ServerStatus {
    private static final AtomicInteger connectedClients = new AtomicInteger(0);

    private static final AtomicInteger requestsReceived = new AtomicInteger(0);

    private static final AtomicInteger createdThreads = new AtomicInteger(0);
    private static final AtomicInteger currentThreads = new AtomicInteger(0);
    private static final Runtime runtime = Runtime.getRuntime();
    private static final OperatingSystemMXBean osMBean = ManagementFactory.getOperatingSystemMXBean();


    public static void clientConnected() {
        connectedClients.incrementAndGet();
    }

    public static void requestReceived() {
        requestsReceived.incrementAndGet();
    }

    public static void threadCreated() {
        currentThreads.incrementAndGet();
        createdThreads.incrementAndGet();
    }

    public static void threadDestroyed() {
        currentThreads.decrementAndGet();
    }

    public static void clientDisconnected() {
        connectedClients.decrementAndGet();
    }

    public static String getStatusReport() {
        String report = "\n=== Status do Servidor ===\n" +
                "Clientes Conectados: " + connectedClients.get() + "\n" +
                "Requisições Recebidas: " + (requestsReceived.get() != 0 ? requestsReceived.get() / 2 : 0) + "\n" +
                "Threads Ativas (PLATAFORMA): " + Thread.activeCount() + "\n" +
                // Ainda não é possivel obter o número de threads (V) em execução no Java, então usamos um contador
                "Threads Atuais: " + currentThreads.get() + "\n" +
                "Threads Criadas: " + (createdThreads.get() != 0 ? createdThreads.get() / 2 : 0) + "\n" +
                "Uso de Memória: " + getMemoryUsage() + "\n" +
                "Carga de CPU: " + getCpuLoad() + "\n" +
                "=====================\n";
        return report;
    }

    private static String getMemoryUsage() {
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return String.format("%.2f MB / %.2f MB",
                usedMemory / (1024.0 * 1024.0),
                totalMemory / (1024.0 * 1024.0));
    }

    private static String getCpuLoad() {
        if (osMBean instanceof com.sun.management.OperatingSystemMXBean sunOsMBean) {
            double processCpuLoad = sunOsMBean.getProcessCpuLoad() * 100;
            return processCpuLoad < 0 ? "N/A" : String.format("%.2f%%", processCpuLoad);
        }
        return "N/A";
    }
}
