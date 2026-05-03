package net.salesianos.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.security.KeyPair;

import net.salesianos.server.thread.ClientHandler;
import net.salesianos.utils.AuctionState;
import net.salesianos.config.AsymmetricCipher;
import net.salesianos.utils.Constants;

public class ServerApp {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            KeyPair serverKeys = AsymmetricCipher.generateKeyPair();

            System.out.println("Subasta iniciada en el puerto " + serverSocket.getLocalPort());

            ArrayList<DataOutputStream> clientsOutputs = new ArrayList<>();
            AuctionState auctionState = new AuctionState(clientsOutputs);
            auctionState.iniciarTemporizador();
            System.out.println("Esperando nuevos postores...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                DataOutputStream clientOutputStream = new DataOutputStream(
                        new BufferedOutputStream(clientSocket.getOutputStream()));
                auctionState.getClients().add(clientOutputStream);

                DataInputStream clientInputStream = new DataInputStream(
                        new BufferedInputStream(clientSocket.getInputStream()));

                ClientHandler clientHandler = new ClientHandler(clientInputStream, clientOutputStream, auctionState, serverKeys);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("[IOE] Error crítico en el servidor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[E] Error inesperado en el servidor: " + e.getMessage());
        }
    }
}