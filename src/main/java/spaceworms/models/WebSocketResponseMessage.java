package spaceworms.models;

public class WebSocketResponseMessage<E> {

    private int status;
    private E content;

    public WebSocketResponseMessage() {}

    public int getStatus() { return this.status; }
    public void setStatus(int status) { this.status = status; }

    public E getContent() { return this.content; }
    public void setContent(E content) { this.content = content; }
}
