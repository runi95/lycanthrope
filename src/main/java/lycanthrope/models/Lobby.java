package lycanthrope.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<User> users = new ArrayList<>();

    @ElementCollection
    @JoinTable
    @Column
    @Enumerated(EnumType.STRING)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Roles> roles = new ArrayList<>();

    @JsonIgnore
    private int neutralOne;

    @JsonIgnore
    private int neutralTwo;

    @JsonIgnore
    private int neutralThree;

    /**
     * state values:
     * 0 = invalid
     * 1 = in lobby
     * 2 = in game (role reveal)
     * 3 = in game (night action)
     * 4 = in game (discussion)
     * 5 = in game (end game vote)
     *
     * Anything above this is invalid
     */
    private int state;

    private String name;

    private int lobbyMaxSize;

    private int currentPlayerSize = 0;

    @JsonIgnore
    private boolean loneWolf;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public int getLobbyMaxSize() { return lobbyMaxSize; }
    public void setLobbyMaxSize(int lobbyMaxSize) { this.lobbyMaxSize = lobbyMaxSize; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Collection<Roles> getRoles() { return roles; }
    public void setRoles(Collection<Roles> roles) { this.roles = roles; }

    public boolean isLoneWolf() { return loneWolf; }
    public void setLoneWolf(boolean loneWolf) { this.loneWolf = loneWolf; }

    public int getNeutralOne() { return neutralOne; }
    public void setNeutralOne(int neutralOne) { this.neutralOne = neutralOne; }

    public int getNeutralTwo() { return neutralTwo; }
    public void setNeutralTwo(int neutralTwo) { this.neutralTwo = neutralTwo; }

    public int getNeutralThree() { return neutralThree; }
    public void setNeutralThree(int neutralThree) { this.neutralThree = neutralThree; }

    public int getCurrentPlayerSize() { return currentPlayerSize; }

    public void addRole(Roles role) {
        this.roles.add(role);
    }

    public void addUser(User user) {
        if (this.users.contains(user))
            return;

        currentPlayerSize++;
        user.setLobby(this);
        this.users.add(user);
    }

    public void removeUser(User user) {
        currentPlayerSize--;
        user.setLobby(null);
        this.users.remove(user);
    }

    @PreRemove
    private void preRemove() {
        for (User u : users) {
            u.setLobby(null);
        }
    }
}
