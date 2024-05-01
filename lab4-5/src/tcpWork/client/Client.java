package tcpWork.client;

import tcpWork.User;
import tcpWork.operations.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private int port = -1;
    private String server;
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Client(String server, int port) {
        this.port = port;
        this.server = server;

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(server, port), 1000);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 7891);
        AddCardOperation op = new AddCardOperation();
        op.getCard().setUser(new User("Denys", "Zlyvko", "M", "13.09.2005"));
        op.getCard().setSerNum("00001");
        op.getCard().setCollege("Karazin University");
        op.getCard().setBalance(25);
        client.applyOperation(op);
        client.applyOperation(new GetCardInfoOperation("00001"));
        client.finish();

        client = new Client("localhost", 7891);
        client.applyOperation(new AddMoneyOperation("00001", 100));
        client.applyOperation(new ShowBalanceOperation("00001"));
        client.applyOperation(new RemoveCardOperation("00001"));
        client.applyOperation(new GetCardInfoOperation("00001"));

        op = new AddCardOperation();
        op.getCard().setUser(new User("Bogdan", "Popovych", "M", "05.04.2005"));
        op.getCard().setSerNum("00002");
        op.getCard().setCollege("Shevchenko University");
        op.getCard().setBalance(50);
        client.applyOperation(op);
        client.applyOperation(new ShowBalanceOperation("00002"));
        client.applyOperation(new PayMoneyOperation("00002", 10));
        client.applyOperation(new GetCardInfoOperation("00002"));
        client.applyOperation(new RemoveCardOperation("00002"));
        client.finish();
    }

    public void finish() {
        try {
            os.writeObject(new StopOperation());
            os.flush();
            System.out.println(is.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: " + ex);
        }
    }

    public void applyOperation(CardOperation op) {
        try {
            os.writeObject(op);
            os.flush();
            System.out.println(is.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error: " + ex);
        }
    }
}