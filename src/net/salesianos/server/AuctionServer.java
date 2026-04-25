package net.salesianos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.salesianos.utils.Constants;

public class AuctionServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            System.out.println("Servidor de subastas iniciado en el puerto " + Constants.SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo postor conectado desde: " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            System.err.println("[IOE]Error en el servidor: " + e.getMessage());
        }
    }
}