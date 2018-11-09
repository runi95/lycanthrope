package lycanthrope.models;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@Table
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "lobby", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @OrderBy("playerNumber ASC")
    private List<User> users = new ArrayList<>();

    @ElementCollection
    @JoinTable
    @Column
    @Enumerated(EnumType.STRING)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Roles> roles = new ArrayList<>();

    private String name;

    private boolean started = false;

    private int lobbyMaxSize;

    private int currentPlayerSize = 0;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public boolean isStarted() { return started; }
    public void setStarted(boolean started) { this.started = started; }

    public int getLobbyMaxSize() { return lobbyMaxSize; }
    public void setLobbyMaxSize(int lobbyMaxSize) { this.lobbyMaxSize = lobbyMaxSize; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Collection<Roles> getRoles() { return roles; }
    public void setRoles(Collection<Roles> roles) { this.roles = roles; }

    public int getCurrentPlayerSize() { return currentPlayerSize; }

    public void addRole(Roles role) {
        this.roles.add(role);
    }

    public void addUser(User user) {
        if (this.users.contains(user))
            return;

        currentPlayerSize++;
        user.setLobby(this);
        user.setPlayerNumber(currentPlayerSize);
        this.users.add(user);
    }

    public void removeUser(User user) {
        currentPlayerSize--;
        user.setLobby(null);
        user.setPlayerNumber(0);
        this.users.remove(user);
    }

    @PreRemove
    private void preRemove() {
        for (User u : users) {
            u.setLobby(null);
        }
    }
}
