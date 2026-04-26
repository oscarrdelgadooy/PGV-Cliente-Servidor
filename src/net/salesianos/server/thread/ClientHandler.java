package net.salesianos.server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.salesianos.utils.AuctionState;
import net.salesianos.utils.FilterChain;

public class ClientHandler extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombre;
    private AuctionState auctionState;

    public ClientHandler(DataInputStream in, String nombre, AuctionState auctionState) {
        this.in = in;
        this.nombre = nombre;
        this.auctionState = auctionState;
        this.out = auctionState.getClients().get(auctionState.getClients().size() - 1);
        broadcast(auctionState.getItemActual().toString());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = in.readUTF();

                if (mensaje.startsWith("COMANDO_")) {
                    FilterChain.procesarComando(mensaje, out, auctionState);
                    continue;
                }
                double puja = Double.parseDouble(mensaje);

                if (auctionState.procesarPuja(puja, nombre)) {
                    broadcast("¡NUEVA PUJA! " + nombre + " ha pujado " + puja + " totis.");
                } else {
                    out.writeUTF("Tu puja de " + puja + " totis es demasiado baja. Mínimo: " + auctionState.getPrecioActual());
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println(nombre + " ha abandonado la subasta.");
        } finally {
            auctionState.getClients().remove(out);
            broadcast(nombre + " se ha ido. El precio sigue en " + auctionState.getPrecioActual() + " totis");
        }
    }

    private void broadcast(String msg) {
        for (DataOutputStream cliente : auctionState.getClients()) {
            try {
                cliente.writeUTF(msg);
                cliente.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a un cliente: \n" + e.getMessage());
            }
        }
    }
}