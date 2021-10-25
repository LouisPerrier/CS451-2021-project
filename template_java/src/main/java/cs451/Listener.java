package cs451;

import cs451.Message;
import cs451.MessageWithId;

public interface Listener {
    void deliver(MessageWithId m, int srcId);
}