package cs451.protocol;

import cs451.Main;
import cs451.Message;
import cs451.MessageWithId;

import java.util.*;

public class FifoBroadcast implements Listener{

    private UniformReliableBroadcast urb;
    private Set<Message> pending;
    private ArrayList<Integer> next;

    public FifoBroadcast(UniformReliableBroadcast urb, int nHosts) {
        this.urb = urb;
        urb.addListener(this);
        pending = new HashSet<>();
        next = new ArrayList<>();
        for (int i = 0 ; i < nHosts ; i++){
            next.add(1);
        }
    }

    public void broadcast(Message m) {
        urb.broadcast(m);
    }


    @Override
    public void deliver(MessageWithId m, int srcId) {
        pending.add(m.message);

        boolean more = true;
        while (more) {
            Message toRemove = null;
            for (Message p : pending) {
                if (p.senderId == m.message.senderId && p.seq == next.get(p.senderId-1)) {
                    int n = next.get(p.senderId-1) + 1;
                    next.set(p.senderId-1, n);
                    toRemove = p;
                    break;
                }
            }
            if (toRemove != null) {
                pending.remove(toRemove);
                Main.outputBuffer.add("d " + toRemove.senderId + " " + toRemove.seq);
            } else {
                more = false;
            }
        }
    }
}
