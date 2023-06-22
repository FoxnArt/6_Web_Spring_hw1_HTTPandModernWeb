//Параметры из Query String парсятся в поле queryParams класса Request,
// в том же классе реализованы методы getQueryParam(String name) и getQueryParams()
package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        final var server = new Server();

        server.addHandler("GET", "/index.html", Main::processFile);
        server.addHandler("GET", "/links.html", Main::processFile);
        server.addHandler("GET", "/spring.png", Main::processFile);
        server.addHandler("GET", "/spring.svg", Main::processFile);
        server.addHandler("GET", "/styles.css", Main::processFile);
        server.addHandler("GET", "/resources.html", Main::processFile);

        server.addHandler("GET", "/messages", (request, out) -> {
             var msg = "Body (method GET)";

                try {
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + "text/plain" + "\r\n" +
                                    "Content-Length: " + msg.length() + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    msg
                    ).getBytes());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
         });

        server.addHandler("POST", "/messages", (request, out) -> {
            var msg = "Body (method POST)";

                try {
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + "text/plain" + "\r\n" +
                                    "Content-Length: " + msg.length() + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n" +
                                    msg
                    ).getBytes());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
         });

        server.listen(9999);
    }

    private static void processFile(Request request, BufferedOutputStream out) {
        try {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);

            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


