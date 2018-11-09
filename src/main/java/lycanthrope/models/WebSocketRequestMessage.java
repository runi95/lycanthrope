package lycanthrope.models;

public class WebSocketRequestMessage<V> {

    private String action;
    private V value;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
