package net.salesianos.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.salesianos.models.Item;

public class AuctionState {
    private AunctionLot aunctionLot = new AunctionLot();
    private Item itemActual;
    private double precioActual;
    private String ganadorActual;
    private List<DataOutputStream> clientsOutputs;

    private long tiempoUltimaPuja;
    private boolean subastaFinalizada = false;

    public AuctionState(ArrayList<DataOutputStream> clientsOutputs) {
        this.itemActual = aunctionLot.getOneItem();
        this.precioActual = itemActual.getPrecioSalida();
        this.ganadorActual = "Nadie";
        this.clientsOutputs = clientsOutputs;
        this.tiempoUltimaPuja = System.currentTimeMillis();
    }

    public Item getItemActual() {
        return itemActual;
    }

    public double getPrecioActual() {
        return precioActual;
    }

    public String getGanadorActual() {
        return ganadorActual;
    }

    public List<DataOutputStream> getClients() {
        return clientsOutputs;
    }

    public synchronized boolean procesarPuja(double betPrice, String postor) {
        if (betPrice > precioActual) {
            this.precioActual = betPrice;
            this.ganadorActual = postor;
            this.tiempoUltimaPuja = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public synchronized void siguienteLote() {
        if (ganadorActual.equals("Nadie")) {
            broadcast("\n[FIN DEL TIEMPO] Nadie ha hecho puja.");
        } else {
            broadcast("\n[FIN DEL TIEMPO] ¡VENDIDO! " + ganadorActual + " se lleva el lote por " + precioActual
                    + " totis.");
        }

        if (aunctionLot.nextItemBet()) {
            this.itemActual = aunctionLot.getOneItem();
            this.precioActual = itemActual.getPrecioSalida();
            this.ganadorActual = "Nadie";
            this.tiempoUltimaPuja = System.currentTimeMillis();
            broadcast(itemActual.toString());
        } else {
            this.subastaFinalizada = true;
            broadcast("\n[ANUNCIO] --- LA SUBASTA HA TERMINADO. ¡GRACIAS POR PARTICIPAR! ---");
            System.out.println("No quedan más lotes. Fin de la subasta.");
        }
    }

    public synchronized void broadcast(String msg) {
        for (DataOutputStream out : clientsOutputs) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje a un cliente: \n" + e.getMessage());
            }
        }
    }

    public void iniciarTemporizador() {
        Thread hiloReloj = new Thread(() -> {
            while (!subastaFinalizada) {
                try {
                    Thread.sleep(1000);
                    long segundosPasados = (System.currentTimeMillis() - tiempoUltimaPuja) / 1000;

                    if (segundosPasados == (Constants.LIMITE_SEGUNDOS - 10)) {
                        broadcast("\n[ALERTA] ¡Faltan 10 segundos para adjudicar el lote a " + ganadorActual + "!");
                    } else if (segundosPasados >= Constants.LIMITE_SEGUNDOS) {
                        siguienteLote();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Temporizador interrumpido.");
                }
            }
        });
        hiloReloj.start();
    }

}