package lycanthrope.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class GameResultPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String nickname;
    private String role;
    private boolean winner;
    private boolean dead;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() { return role; }
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isWinner() {
        return winner;
    }
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isDead() {
        return dead;
    }
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameResultPlayer)) return false;
        GameResultPlayer that = (GameResultPlayer) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
