package cs451;

import cs451.FairLossLink;

public class ReceiveThread extends Thread {

    private FairLossLink fairlossLink;

    public ReceiveThread(FairLossLink fairlossLink) {
        this.fairlossLink = fairlossLink;
    }

    @Override
    public void run() {
        while (true) {
            fairlossLink.receive();
        }
    }
}