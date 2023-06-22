package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        final var server = new Server();

        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                var msg = "Body (method GET)";

                try {
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + "text/plain" + "\r\n" +
                                    "Content-Length: " + msg.length() + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    msg
                    ).getBytes());
                    responseStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.addHandler("GET", "/spring.svg", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                try {
                    final var filePath = Path.of(".", "public", request.getPath());
                    final var mimeType = Files.probeContentType(filePath);

                    final var length = Files.size(filePath);
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    Files.copy(filePath, responseStream);
                    responseStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                var msg = "Body (method POST)";

                try {
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + "text/plain" + "\r\n" +
                                    "Content-Length: " + msg.length() + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    msg
                    ).getBytes());
                    responseStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.listen(9999);
    }
}


