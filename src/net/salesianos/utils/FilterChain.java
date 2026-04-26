package net.salesianos.utils;

import java.io.DataOutputStream;
import java.io.IOException;

import net.salesianos.server.ServerApp;

public class FilterChain {
    public static boolean filter_chain(String input, DataOutputStream out) throws IOException {
        input = input.trim().toLowerCase();

        if (input.equals("salir")) {
            return false;
        }

        if (input.isEmpty()) {
            System.out.print("Tu puja -> ");
            return true;
        }

        if (input.equals("precio")) {
            out.writeUTF("COMANDO_PRECIO");
            out.flush();
            return true;
        }

        if (input.equals("ganador")) {
            out.writeUTF("COMANDO_GANADOR");
            out.flush();
            return true;
        }

        if (input.equals("ayuda")) {
            out.writeUTF("COMANDO_AYUDA");
            out.flush();
            return true;
        }

        try {
            Double.parseDouble(input);
            out.writeUTF(input);
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println(
                    "[ERROR] Entrada inválida. Usa números para pujar, 'precio', 'ganador', 'ayuda' o 'salir'.");
        }

        return true;
    }

    public static void procesarComando(String comando, DataOutputStream out) throws IOException {
        switch (comando) {
            case "COMANDO_PRECIO":
                out.writeUTF("El precio actual es: " + ServerApp.precioActual + " totis.");
                break;
            case "COMANDO_GANADOR":
                out.writeUTF("El ganador actual es: " + ServerApp.ganadorActual);
                break;
            case "COMANDO_AYUDA":
                out.writeUTF(
                        "Comandos disponibles:\n- precio: Muestra el precio actual.\n- ganador: Muestra el ganador actual.\n- salir: Abandona la subasta.");
                break;
            default:
                out.writeUTF("Comando desconocido.");
        }
        out.flush();
    }
}