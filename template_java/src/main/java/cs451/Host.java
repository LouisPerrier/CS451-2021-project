package cs451;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Host {

    private static final String IP_START_REGEX = "/";

    private int id;
    private String ip;
    private int port = -1;

    private ReceiveThread receiveThread;
    private Host receiver;
    private int nbHosts;
    private int nbMessages;
    private PerfectLink perfectLink;

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = Integer.parseInt(idString);

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }

            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void init(List<Host> hosts, int nbMessages, Host receiver) {
        nbHosts = hosts.size();
        this.receiver = receiver;

        FairLossLink fairlossLink = new FairLossLink(ip, port, hosts);
        perfectLink = new PerfectLink(fairlossLink, hosts);

        receiveThread = new ReceiveThread(fairlossLink);

        this.nbMessages = nbMessages;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void broadcast() {
        receiveThread.start();

        for (int i =1 ; i<=nbMessages ; i++) {
            perfectLink.send(new Message(i, id, new int[nbHosts]), receiver.getIp(), receiver.getPort());
	
            Main.outputBuffer.add("b " + i);
        }
    }

}
