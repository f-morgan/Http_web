package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public void listen(int port) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(64);
        ServerSocket serverSocket = new ServerSocket(port);
        try {
          while (true) {
              Socket socket = serverSocket.accept();
              pool.submit(() -> processingPath(socket));
          }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void processingPath(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                out.println("Echo: " + line + " serverTime = " + System.currentTimeMillis());
                if (line.equals("end")) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
