package tcpWork.operations;

public class RemoveCardOperation extends CardOperation {
    private String serNum;

    public RemoveCardOperation(String serNum) {
        this.serNum = serNum;
    }

    public RemoveCardOperation() {
    }

    public String getSerNum() {
        return serNum;
    }

    public void setSerNum(String serNum) {
        this.serNum = serNum;
    }
}