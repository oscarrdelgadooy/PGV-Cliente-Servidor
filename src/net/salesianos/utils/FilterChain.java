package net.salesianos.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class FilterChain {

    public static boolean filter_chain(String input, DataOutputStream out) throws IOException {
        input = input.trim().toLowerCase();

        if (input.isEmpty()) {
            System.out.print("Tu puja -> ");
            return true;
        }

        if (input.equals("salir")) {
            return false;
        }

        for (String command : Constants.CMD_COMMANDS) {
            if (input.equals(command)) {
                out.writeUTF("COMANDO_" + command.toUpperCase());
                out.flush();
                return true;
            }
        }
        try {
            Double.parseDouble(input);
            out.writeUTF(input);
            out.flush();
        } catch (NumberFormatException nfe) {
            System.out.println(
                    "[ERROR] Entrada inválida. Usa números para pujar, 'precio', 'ganador', 'ayuda' o 'salir'.");
            System.out.print("Tu puja -> ");
        }
        return true;
    }

    public static void procesarComando(String comando, DataOutputStream out, AuctionState auctionState) throws IOException {
        switch (comando) {
            case "COMANDO_PRECIO":
                out.writeUTF("El precio actual es: " + auctionState.getPrecioActual() + " totis.");
                break;
            case "COMANDO_GANADOR":
                out.writeUTF("El ganador actual es: " + auctionState.getGanadorActual());
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