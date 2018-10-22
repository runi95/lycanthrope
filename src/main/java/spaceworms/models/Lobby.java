package spaceworms.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Lobby {

    private boolean started = false;

    // Hardcoded to let player 1 start, maybe it should be random instead?
    private int currentPlayerId = 1;

    @Id
    private int id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "lobby", cascade = CascadeType.ALL)
    @OrderBy("playerNumber ASC")
    private List<User> users = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    private Board board;

    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isStarted() { return started; }
    public void setStarted(boolean started) { this.started = started; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public int getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(int currentPlayerId) { this.currentPlayerId = currentPlayerId; }

    public void addUser(User user) {
        user.setLobby(this);
        this.users.add(user);
    }

    public void removeUser(User user) {
        user.setLobby(null);
        this.users.remove(user);
    }
}
