package cs451;

import cs451.Host;
import cs451.Message;
import cs451.MessageWithId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PerfectLink implements Listener {

    private FairLossLink fairLossLink;
    private List<Host> hosts;
    private int nHosts;
    private Timer timer;

    private ConcurrentHashMap<MessageWithId, AbstractMap.SimpleEntry<String, Integer>> unAcked;
    private Set<MessageWithId> delivered;

    private final long period = 1000;

    public PerfectLink(FairLossLink fairLossLink, List<Host> hosts){
        this.fairLossLink = fairLossLink;
        this.hosts = hosts;
        this.nHosts = hosts.size();
        fairLossLink.addListener(this);

        unAcked = new ConcurrentHashMap<>();
        delivered = new HashSet<>();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (MessageWithId m : unAcked.keySet()) {
                    AbstractMap.SimpleEntry<String, Integer> e = unAcked.get(m);
                    if (e != null)
                        fairLossLink.send(m, e.getKey(), e.getValue());
                }
            }
        }, 0, period);
    }

    public void send(Message m, String dstIp, int dstPort) {
        MessageWithId m1 = new MessageWithId(m, UUID.randomUUID());
        unAcked.put(m1, new AbstractMap.SimpleEntry<>(dstIp, dstPort));
        fairLossLink.send(m1, dstIp, dstPort);
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        if (m.message.seq == -1) { //ack
            unAcked.remove(m);
        } else {
            String srcIp = "";
            int srcPort = 0;
            for (Host h : hosts) {
                if (h.getId() == srcId) {
                    srcIp = h.getIp();
                    srcPort = h.getPort();
                }
            }
            MessageWithId ack = new MessageWithId(new Message(-1, srcId, new int[nHosts]), m.uuid);
            fairLossLink.send(ack, srcIp, srcPort);

            if (!delivered.contains(m)) {
                delivered.add(m);
                listener.deliver(m, srcId);
                Main.outputBuffer.add("d " + m.senderId + " " + m.seq)
            }
        }
    }
}