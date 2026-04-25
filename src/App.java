import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import net.salesianos.utils.Constants;

public class App {

    public static double actual_price = Constants.PRECIO_INICIAL;
    public static String actual_winner = "Nadie";

    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            System.out.println("Subasta iniciada en el puerto " + serverSocket.getLocalPort());

            ArrayList<DataOutputStream> clientsOutputs = new ArrayList<>();

            while (true) {
                System.out.println("Esperando nuevos postores...");
                Socket clientSocket = serverSocket.accept();

                DataOutputStream clientOutputStream = new DataOutputStream(
                        new BufferedOutputStream(clientSocket.getOutputStream()));

                clientsOutputs.add(clientOutputStream);

                DataInputStream clientInputStream = new DataInputStream(
                        new BufferedInputStream(clientSocket.getInputStream()));

                String name = clientInputStream.readUTF();

                System.out.println(name + " ha entrado a la subasta.");

            }
        } catch (IOException e) {
            System.err.println("[IOE]Error crítico en el servidor (App): " + e.getMessage());
        }

    }

}
