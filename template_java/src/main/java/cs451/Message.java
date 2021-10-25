package cs451;

public class Message {
    public int seq;
    public int senderId;
    public int[] vectorClock;

    public Message(int seq, int senderId, int[] vectorClock) {
        this.seq = seq;
        this.senderId = senderId;
        this.vectorClock = vectorClock;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (! (o instanceof Message)) return false;

        Message m = (Message) o;

        return this.seq == m.seq && this.senderId == m.senderId;
    }

    @Override
    public int hashCode() {
        return seq;
    }
}