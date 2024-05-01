package tcpWork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MetroServer extends Thread {
    MetroCardBank bank;
    private ServerSocket serverSocket = null;
    private int serverPort = -1;

    public MetroServer(int port) {
        this.bank = new MetroCardBank();
        this.serverPort = port;
    }

    public static void main(String[] args) {
        MetroServer server = new MetroServer(7891);
        server.start();
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
            System.out.println("Metro Server started");
            while (true) {
                System.out.println("New Client Waiting...");
                Socket sock = serverSocket.accept();
                System.out.println("New client: " + sock);
                ClientHandler ch = new ClientHandler(this.getBank(), sock);
                ch.start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                serverSocket.close();
                System.out.println("Metro Server stopped");
            } catch (IOException ex) {
                System.out.println("Error: " + ex);
            }
        }
    }

    private MetroCardBank getBank() {
        return this.bank;
    }
}
