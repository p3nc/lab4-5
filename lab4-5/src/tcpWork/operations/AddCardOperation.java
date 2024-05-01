package tcpWork.operations;

import tcpWork.MetroCard;

public class AddCardOperation extends CardOperation {
    private MetroCard card;

    public AddCardOperation() {
        card = new MetroCard();
    }

    public MetroCard getCard() {
        return card;
    }
}