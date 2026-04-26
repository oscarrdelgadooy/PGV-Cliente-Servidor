package net.salesianos.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import net.salesianos.models.Item;
import net.salesianos.server.thread.ClientHandler;
import net.salesianos.utils.AunctionLot;
import net.salesianos.utils.Constants;

public class ServerApp {
    private static AunctionLot aunctionLot = new AunctionLot();

    public static Item itemActual = aunctionLot.getOneItem();

    public static double precioActual = itemActual.getPrecioSalida();
    public static String ganadorActual = "Nadie";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            System.out.println("Subasta iniciada en el puerto " + serverSocket.getLocalPort());

            ArrayList<DataOutputStream> clientsOutputs = new ArrayList<>();

            System.out.println(itemActual.toString());
            System.out.println("Esperando nuevos postores...");
            while (true) {
                Socket clientSocket = serverSocket.accept();

                DataOutputStream clientOutputStream = new DataOutputStream(
                        new BufferedOutputStream(clientSocket.getOutputStream()));
                clientsOutputs.add(clientOutputStream);

                DataInputStream clientInputStream = new DataInputStream(
                        new BufferedInputStream(clientSocket.getInputStream()));
                String name = clientInputStream.readUTF();

                System.out.println(name + " ha entrado a la subasta.");

                ClientHandler clientHandler = new ClientHandler(clientInputStream, name, clientsOutputs);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("[IOE]Error crítico en el servidor: " + e.getMessage());
        }
    }

    public static synchronized boolean procesarPuja(double nuevaPuja, String nombrePostor) {
        if (nuevaPuja > precioActual) {
            precioActual = nuevaPuja;
            ganadorActual = nombrePostor;
            return true;
        }
        return false;
    }
}