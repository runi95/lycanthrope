package lycanthrope.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Lobby lobby;

    private int playerNumber;

    private int squareNumber;

    // Just in case we want to add roles at some point.
    private String role = "default";

    public long getId() { return id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Lobby getLobby() { return lobby; }
    public void setLobby(Lobby lobby) { this.lobby = lobby; }

    public int getSquareNumber() { return squareNumber; }
    public void setSquareNumber(int squareNumber) { this.squareNumber = squareNumber; }

    public int getPlayerNumber() { return playerNumber; }
    public void setPlayerNumber(int playerNumber) { this.playerNumber = playerNumber; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", lobby=" + lobby +
                ", playerNumber=" + playerNumber +
                ", squareNumber=" + squareNumber +
                ", role='" + role + '\'' +
                '}';
    }
}
