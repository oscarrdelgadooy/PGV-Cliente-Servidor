package net.salesianos.server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.salesianos.server.ServerApp;

public class ClientHandler extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombre;
    private ArrayList<DataOutputStream> listaClientes;

    public ClientHandler(DataInputStream in, String nombre, ArrayList<DataOutputStream> listaClientes) {
        this.in = in;
        this.nombre = nombre;
        this.listaClientes = listaClientes;
        this.out = listaClientes.get(listaClientes.size() - 1);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = in.readUTF();
                double puja = Double.parseDouble(mensaje);

                if (ServerApp.procesarPuja(puja, nombre)) {
                    broadcast("¡NUEVA PUJA! " + nombre + " ha pujado " + puja + "€");
                } else {
                    out.writeUTF("Tu puja de " + puja + "€ es demasiado baja. Mínimo: " + ServerApp.precioActual);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println(nombre + " ha abandonado la subasta.");
        } finally {
            listaClientes.remove(out);
            broadcast(nombre + " se ha ido. El precio sigue en " + ServerApp.precioActual + "€");
        }
    }

    private void broadcast(String msg) {
        for (DataOutputStream cliente : listaClientes) {
            try {
                cliente.writeUTF(msg);
                cliente.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a un cliente: \n" + e.getMessage());
            }
        }
    }
}