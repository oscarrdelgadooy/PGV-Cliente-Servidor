package net.salesianos.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import net.salesianos.client.thread.ServerListener;
import net.salesianos.utils.Constants;

public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre de postor: ");
        String name = scanner.nextLine();

        try (Socket socket = new Socket("localhost", Constants.SERVER_PORT)) {
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            outputStream.writeUTF(name);
            outputStream.flush();

            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            ServerListener listener = new ServerListener(inputStream);
            listener.start();

            System.out.println("Conectado! Introduce tu puja (número):");

            while (true) {
                System.out.print("Tu puja -> ");
                String input = scanner.nextLine();
                try {
                    Double.parseDouble(input);
                    outputStream.writeUTF(input);
                    outputStream.flush();
                } catch (NumberFormatException nfe) {
                    System.out.println("[NumberFormatE.] Hay que introducir un número valido (ej: 150.5).");
                }catch (IOException ioe) {
                    System.out.println("[IOE] Error al enviar la puja: " + ioe.getMessage());
                }catch (IllegalStateException itse) {
                    System.out.println("[IllegalThreadStateE.] Error scanner cerrado: " + itse.getMessage());
                }
            }
        } catch (IOException ioe) {
            System.out.println("[IOE] No se pudo conectar" + ioe.getMessage());
        } catch (IllegalThreadStateException itse) {
            System.out.println("[IllegalThreadStateE.] Error hilo ya empezado: " + itse.getMessage());
        } finally {
            scanner.close();
            System.out.println("Has abandonado la sala de subastas.");
        }
    }
}