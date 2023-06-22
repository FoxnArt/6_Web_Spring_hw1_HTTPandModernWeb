package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) throws IOException {
        var serverSocket = new ServerSocket(port);
        System.out.println("Запускаем сервер на порту " + port);
        System.out.println("Откройте в браузере http://localhost:" + port + "/");

        ExecutorService threadPool = Executors.newFixedThreadPool(64);

        while (true) {
            try {
                var socket = serverSocket.accept();
                threadPool.submit(() -> handlingConnection(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlingConnection(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            final var requestLine = in.readLine();

            if (requestLine == null)
                return;

            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                badRequest(out);
                return;
            }

            final var request = new Request(parts[0], parts[1]);

            //тестовый вывод
            System.out.println("path: " + request.getPath());
            System.out.println("query params: " + request.getQueryParams());
            System.out.println("name: " + request.getQueryParam("vze"));

            if (!handlers.containsKey(request.getMethod())) {
                notFound(out);
                return;
            }

            var methodHandlers = handlers.get(request.getMethod());

            if (!methodHandlers.containsKey(request.getPath())) {
                notFound(out);
                return;
            }

            var handler = methodHandlers.get(request.getPath());

            if (handler == null) {
                notFound(out);
                return;
            }

            handler.handle(request, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        handlers.get(method).put(path, handler);
    }
}

