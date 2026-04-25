package net.salesianos.client.thread;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerListener extends Thread {
    private DataInputStream inputStream;

    public ServerListener(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String serverMessage = this.inputStream.readUTF();
                System.out.println("\n[ANUNCIO] " + serverMessage);
                System.out.print("Tu puja -> ");
            }
        } catch (IOException e) {
            System.out.println("[IOE] Conexión cerrada por el servidor.");
        }
    }
}