package cs451.protocol;

import cs451.Main;
import cs451.Message;
import cs451.MessageWithId;

import java.util.*;

public class UniformReliableBroadcast extends UnderlyingProtocol implements Listener {

    private BestEffortBroadcast beb;

    private final int nHosts;

    private Set<Message> delivered, pending;
    private Map<Message, Set<Integer>> ack;

    public UniformReliableBroadcast(BestEffortBroadcast beb,  int nHosts) {
        this.beb = beb;
        beb.addListener(this);

        this.nHosts = nHosts;

        delivered = new HashSet<>();
        pending = new HashSet<>();
        ack = new HashMap<>();
    }

    public void broadcast(Message m) {

        pending.add(m);
        beb.broadcast(m);
        checkAndDeliver(m);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        Message message = m.message;
        if (!ack.containsKey(message)) {
            ack.put(message, new HashSet<>());
        }
        ack.get(message).add(srcId);


        if (!pending.contains(message)) {
            pending.add(message);
            beb.broadcast(message);
        }
        checkAndDeliver(message);
    }

    private void checkAndDeliver(Message m) {
        if (ack.containsKey(m) && ack.get(m).size() > nHosts/2 && !delivered.contains(m)) {
            delivered.add(m);
            listener.deliver(new MessageWithId(m, null), m.senderId);
        }
    }
}
