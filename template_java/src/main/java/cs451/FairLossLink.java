package cs451;

import cs451.Message;
import cs451.Host;
import cs451.MessageWithId;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class FairLossLink extends UnderlyingProtocol {

    private DatagramSocket socket;
    private List<Host> hosts;
    private int nHosts;

    public FairLossLink(String ip, int port, List<Host> hosts) {
        try {
            this.socket = new DatagramSocket(port, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.hosts = hosts;
        nHosts = hosts.size();
    }


    public void send(MessageWithId m, String dstIp, int dstPort) {

        ByteBuffer bb = ByteBuffer.allocate(24+4*nHosts);
        bb.putInt(m.message.seq).putInt(m.message.senderId);
        bb.putLong(m.uuid.getMostSignificantBits());
        bb.putLong(m.uuid.getLeastSignificantBits());
        for (int i = 0; i < nHosts; i++) {
            bb.putInt(m.message.vectorClock[i]);
        }
        byte[] buf = bb.array();

        try {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(dstIp), dstPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receive() {
        byte[] buf = new byte[24+4*nHosts];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        try {
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        int seq = bb.getInt();
        int senderId = bb.getInt();
        long uuid1 = bb.getLong();
        long uuid2 = bb.getLong();
        int[] vectorClock = new int[nHosts];
        for (int i=0 ; i<nHosts ; i++){
            vectorClock[i] = bb.getInt();
        }

        Message message = new Message(seq, senderId, vectorClock);
        MessageWithId m = new MessageWithId(message, new UUID(uuid1, uuid2));

        int sourceId = 0;
        for (Host h : hosts) {
            if (h.getIp().equals(packet.getAddress().getHostAddress()) && h.getPort() == packet.getPort()) {
                sourceId = h.getId();
            }
        }

        listener.deliver(m, sourceId);
    }

}