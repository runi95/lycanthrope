package spaceworms.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Lobby {

    @Id
    private int id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "lobby", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    private int lobbySize = 0;

    private boolean started = false;

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

    public void addUser(User user) {
        this.lobbySize = this.lobbySize + 1;
        user.setLobby(this);
        this.users.add(user);
    }

    public int getLobbySize() { return this.lobbySize; }
}
