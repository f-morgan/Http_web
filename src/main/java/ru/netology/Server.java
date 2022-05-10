package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int bufferSize = 256;
    private final int defaultThreadPoolNumber = 64;
    private final int threadPoolNumber;
    private final ConcurrentHashMap<Method, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();


    public Server() {
        this.threadPoolNumber = defaultThreadPoolNumber;
    }

    public Server(int threadPoolNumber) {
        this.threadPoolNumber = threadPoolNumber;
    }

    public void listen(int port) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(threadPoolNumber);
        ServerSocket serverSocket = new ServerSocket(port);
        try {
          while (true) {
              Socket socket = serverSocket.accept();
              pool.submit(() -> processRequest(socket));
          }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void processRequest(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            CharBuffer buffer = CharBuffer.allocate(bufferSize);
            StringBuilder builder = new StringBuilder();

            while (in.ready()) {
                in.read(buffer);
                buffer.flip();
                builder.append(buffer);
                buffer.clear();
            }

            Request request = new Request(builder.toString());

            ConcurrentHashMap<String, Handler> pathsAndHandlers = handlers.get(request.getMethod());
            if (pathsAndHandlers != null) {
                String url = request.getUrl().split("\\?",2)[0];
                Handler handler = pathsAndHandlers.get(url);
                if (handler != null) {
                    handler.handle(request, out);
                    return;
                }else{
                    answer404NotFound(out);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answer404NotFound(BufferedOutputStream out) throws IOException {

        Response response = new Response();
        response.setStatusCode(404);
        response.setStatus("Not found");
        response.addHeader("Content-Type", "text/html; charset=utf-8");
        response.setBody("<html><body><h1>Resource not found</h1></body><html>".getBytes(StandardCharsets.UTF_8));
        out.write(response.getMessageHeaders());
        out.write(response.getMessageBody());
        out.flush();
    }

    public void addHandler(Method method, String path, Handler handler) {
            ConcurrentHashMap<String, Handler> pathsAndHandlers = handlers.get(method);
            if (pathsAndHandlers == null) {
                pathsAndHandlers = new ConcurrentHashMap<String, Handler>();
                handlers.put(method, pathsAndHandlers);
            }
            pathsAndHandlers.put(path, handler);
    }
}
