package net.salesianos.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

import net.salesianos.client.thread.ServerListener;
import net.salesianos.config.AsymmetricCipher;
import net.salesianos.utils.Constants;
import net.salesianos.utils.FilterChain;

public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce tu nombre de postor: ");
        String name = scanner.nextLine();

        try (Socket socket = new Socket("localhost", Constants.SERVER_PORT)) {
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String serverPubKeyString = inputStream.readUTF();
            PublicKey serverPublicKey = AsymmetricCipher.getPublicKeyFromString(serverPubKeyString);

            String nombreCifrado = AsymmetricCipher.encrypt(name, serverPublicKey);
            outputStream.writeUTF(nombreCifrado);
            outputStream.flush();

            ServerListener listener = new ServerListener(inputStream);
            listener.start();

            System.out.println("Conectado! Disfruta de tu estancia:");

            System.out.print("Tu puja -> ");
            while (true) {
                String input = scanner.nextLine();
                try {
                    if (!FilterChain.filter_chain(input, outputStream, serverPublicKey)) {
                        break;
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("[NumberFormatE.] Hay que introducir un número valido (ej: 150.5).");
                } catch (IOException ioe) {
                    System.out.println("[IOE] Error al enviar la puja: " + ioe.getMessage());
                } catch (IllegalStateException itse) {
                    System.out.println("[IllegalThreadStateE.] Error scanner cerrado: " + itse.getMessage());
                }
            }
        } catch (IOException ioe) {
            System.out.println("[IOE] No se pudo conectar: " + ioe.getMessage());
        } catch (IllegalThreadStateException itse) {
            System.out.println("[IllegalThreadStateE.] Error hilo ya empezado: " + itse.getMessage());
        } catch (Exception e) {
            System.out.println("[Error] No se pudo enviar el mensaje." + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Has abandonado la sala de subastas.");
        }
    }
}