package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server();

        server.addHandler(Method.GET, "/links.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/spring.png", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);

            }
        });

        server.addHandler(Method.GET, "/spring.svg", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/events.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/events.js", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/app.js", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/styles.css", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.addHandler(Method.GET, "/classic.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                Response response = new Response();
                Path filePath = Path.of(".", "public", request.getUrl());
                String mimeType = Files.probeContentType(filePath);
                response.setMimeType(mimeType);
                String template = Files.readString(filePath, StandardCharsets.UTF_8);
                String result = template.replace("{time}",LocalDateTime.now().toString());
                response.setBody(result.getBytes(StandardCharsets.UTF_8));
                responseStream.write(response.getMessageHeaders());
                responseStream.write(response.getMessageBody());
                responseStream.flush();
            }
        });

        server.addHandler(Method.POST, "/forms.html", new Handler() {
            // TODO: handlers code
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                Response response = new Response();
                response.setMimeType("text/html; charset=utf-8");
                String bodyString = request.getBody();
                String[] loginParts = bodyString.split("&");
                String[] login = loginParts[0].split("=");
                String[] password = loginParts[1].split("=");
                if (login[0].equals("login")
                        &&login[1].equals("admin")
                        &&password[0].equals("password")
                        &&password[1].equals("qwerty")) {

                    response.setBody("<html><body><h1>Access granted.</h1></body><html>".getBytes(StandardCharsets.UTF_8));
                } else {
                    response.setStatusCode(403);
                    response.setStatus("Forbidden");
                    response.setBody("<html><body><h1>Access denied.</h1></body><html>".getBytes(StandardCharsets.UTF_8));
                }
                responseStream.write(response.getMessageHeaders());
                responseStream.write(response.getMessageBody());
                responseStream.flush();
            }
        });

        server.addHandler(Method.GET, "/forms.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                makeResponse(request, responseStream);
            }
        });

        server.listen(9999);
    }

    private static void makeResponse(Request request, BufferedOutputStream responseStream) throws IOException {
        Response response = new Response();
        Path filePath = Path.of(".", "public", request.getUrl());
        String mimeType = Files.probeContentType(filePath);
        response.setMimeType(mimeType);
        response.setBody(Files.readAllBytes(filePath));
        responseStream.write(response.getMessageHeaders());
        responseStream.write(response.getMessageBody());
        responseStream.flush();
    }
}

