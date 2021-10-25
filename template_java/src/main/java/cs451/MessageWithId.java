package cs451;

import java.util.UUID;

public class MessageWithId {
    public Message message;
    public UUID uuid;

    public MessageWithId(Message message, UUID uuid) {
        this.message = message;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (! (o instanceof MessageWithId)) return false;

        MessageWithId m = (MessageWithId) o;

        return this.uuid.equals(m.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }


}