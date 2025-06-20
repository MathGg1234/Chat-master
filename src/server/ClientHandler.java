package server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * Handles communication with a single client.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final List<PrintWriter> clientWriters;
    private PrintWriter out;

    public ClientHandler(Socket socket, List<PrintWriter> clientWriters) {
        this.clientSocket = socket;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Add writer to the shared list
            synchronized (clientWriters) {
                clientWriters.add(out);
            }

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Received: " + msg);

                // Expect encryptedUsername::encryptedMessage
                // si le message est pas chiffré déco le mec
                String[] parts = msg.split("==::", 2);
                if (parts.length != 2) {
                    System.out.println("Invalid message received");

                    // Donne l'ip du message
                    InetAddress clientAddress = clientSocket.getInetAddress();
                    String ip = clientAddress.getHostAddress();

                    System.out.println("Client IP: " + ip);
                    out.println("Ton message est en clair nigaud");
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println("Error closing client socket.");
                    }
                    break;
                }

                // Broadcast to all clients
                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(msg);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            // Remove writer and close socket
            synchronized (clientWriters) {
                clientWriters.remove(out);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket.");
            }
        }
    }
}