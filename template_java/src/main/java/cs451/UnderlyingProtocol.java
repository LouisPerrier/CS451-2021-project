package cs451;

public abstract class UnderlyingProtocol {
    protected Listener listener;

    public void addListener(Listener listener) {
        this.listener = listener;
    }
}