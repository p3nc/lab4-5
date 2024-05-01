package tcpWork;

import tcpWork.operations.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final MetroCardBank bank;
    private final Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private boolean work;


    public ClientHandler(MetroCardBank bank, Socket socket) {
        this.bank = bank;
        this.socket = socket;
        this.work = true;

        try {
            this.is = new ObjectInputStream(socket.getInputStream());
            this.os = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    @Override
    public void run() {
        synchronized (bank) {
            System.out.println("Client Handler Started for: " + socket + " on :" + socket.getPort());

            while (work) {
                Object obj;
                try {
                    obj = is.readObject();
                    processOperation(obj);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error: " + e);
                }
            }

            try {
                System.out.println("Client Handler Stopped for: " + socket);
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error: " + ex);
            }
        }
    }

    private void processOperation(Object obj) throws IOException, ClassNotFoundException {
        if (obj instanceof StopOperation) {
            finish();
        } else if (obj instanceof AddCardOperation) {
            addCard(obj);
        } else if (obj instanceof AddMoneyOperation) {
            addMoney(obj);
        } else if (obj instanceof PayMoneyOperation) {
            payMoney(obj);
        } else if (obj instanceof RemoveCardOperation) {
            removeCard(obj);
        } else if (obj instanceof ShowBalanceOperation) {
            showBalance(obj);
        } else if (obj instanceof GetCardInfoOperation) {
            getCardInfo(obj);
        } else {
            error();
        }
    }

    private void finish() throws IOException {
        work = false;
        os.writeObject("Finish Work " + socket);
        os.flush();
    }

    private void addCard(Object obj) throws IOException {
        bank.addCard(((AddCardOperation) obj).getCard());
        os.writeObject("Card Added");
        os.flush();
    }

    private void addMoney(Object obj) throws IOException {
        AddMoneyOperation op = (AddMoneyOperation) obj;
        boolean res = bank.addMoney(op.getSerNum(), op.getMoney());
        if (res) {
            os.writeObject("Balance Added");
            os.flush();
        } else {
            os.writeObject("Cannot Balance Added");
            os.flush();
        }
    }

    private void payMoney(Object obj) throws IOException {
        PayMoneyOperation op = (PayMoneyOperation) obj;
        boolean res = bank.getMoney(op.getSerNum(), op.getAmount());
        if (res) {
            os.writeObject("Money Payed");
            os.flush();
        } else {
            os.writeObject("Cannot Pay Money");
            os.flush();
        }
    }

    private void removeCard(Object obj) throws IOException {
        RemoveCardOperation op = (RemoveCardOperation) obj;
        boolean res = bank.removeCard(op.getSerNum());
        if (res) {
            os.writeObject("Metro Card Successfully Remove: " + op.getSerNum());
            os.flush();
        } else {
            os.writeObject("Cannot Remove Card" + op.getSerNum());
            os.flush();
        }
    }

    private void showBalance(Object obj) throws IOException {
        ShowBalanceOperation op = (ShowBalanceOperation) obj;
        double balance = bank.getBalance(op.getSerNum());
        if (balance >= 0) {
            os.writeObject("Card : " + op.getSerNum() + " balance: " + balance);
            os.flush();
        } else {
            os.writeObject("Cannot Show Balance for Card: " + op.getSerNum());
        }
    }

    private void getCardInfo(Object operation) throws IOException {
        GetCardInfoOperation cardInfoOperation = (GetCardInfoOperation) operation;
        String info = bank.displayCardInfo(cardInfoOperation.getSerialNumber());

        if (info != null) {
            os.writeObject(info);
            os.flush();
        } else {
            os.writeObject("No info was found");
        }
    }

    private void error() throws IOException {
        os.writeObject("Bad Operation");
        os.flush();
    }
}