package cs451.protocol;

import cs451.Main;
import cs451.Message;
import cs451.Host;
import cs451.MessageWithId;

import java.util.List;

public class BestEffortBroadcast extends UnderlyingProtocol implements Listener{

    private PerfectLink perfectLink;
    private List<Host> hosts;

    public BestEffortBroadcast(PerfectLink perfectLink, List<Host> hosts) {
        this.perfectLink = perfectLink;
        perfectLink.addListener(this);
        this.hosts = hosts;
    }

    public void broadcast(Message m) {
        for (Host h : hosts) {
            perfectLink.send(m, h.getIp(), h.getPort());
        }
    }

    @Override
    public void deliver(MessageWithId m, int srcId) {
        listener.deliver(m, srcId);
    }
}
