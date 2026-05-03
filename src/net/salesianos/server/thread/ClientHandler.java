package net.salesianos.server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;

import net.salesianos.config.AsymmetricCipher;
import net.salesianos.utils.AuctionState;
import net.salesianos.utils.FilterChain;

public class ClientHandler extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombre;
    private AuctionState auctionState;
    private KeyPair serverKeys;

    public ClientHandler(DataInputStream in, DataOutputStream out, AuctionState auctionState, KeyPair serverKeys) {
        this.in = in;
        this.out = out;
        this.auctionState = auctionState;
        this.serverKeys = serverKeys;
    }

    @Override
    public void run() {
        try {
            String publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(serverKeys.getPublic().getEncoded());
            out.writeUTF(publicKeyBase64);
            out.flush();

            String nombreCifrado = in.readUTF();
            this.nombre = AsymmetricCipher.decrypt(nombreCifrado, serverKeys.getPrivate());

            System.out.println(this.nombre + " ha entrado a la subasta (Conexión Segura).");

            out.writeUTF(auctionState.getItemActual().toString());
            out.flush();

            while (true) {
                String codedMessage = in.readUTF();

                String mensaje = AsymmetricCipher.decrypt(codedMessage, serverKeys.getPrivate());

                if (mensaje.startsWith("COMANDO_")) {
                    FilterChain.procesarComando(mensaje, out, auctionState);
                    continue;
                }
                double puja = Double.parseDouble(mensaje);

                if (auctionState.procesarPuja(puja, nombre)) {
                    broadcast("¡NUEVA PUJA! " + nombre + " ha pujado " + puja + " totis.");
                } else {
                    out.writeUTF("Tu puja de " + puja + " totis es demasiado baja. Mínimo: "
                            + auctionState.getPrecioActual());
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println(nombre + " ha abandonado la subasta abruptamente.");
        } catch (Exception e) {
            System.out.println("Error de descifrado o seguridad con " + nombre + ": " + e.getMessage());
        } finally {
            auctionState.getClients().remove(out);
            if (nombre != null) {
                broadcast(nombre + " se ha desconectado. El precio sigue en " + auctionState.getPrecioActual()
                        + " totis");
            }
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