package lycanthrope.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDateTime gameEndTime;

    private String winningTeam;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GameResultPlayer> gameResultPlayers = new ArrayList<>();

    public long getId() { return id; }

    public LocalDateTime getGameEndTime() {
        return gameEndTime;
    }
    public void setGameEndTime(LocalDateTime gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public String getWinningTeam() { return winningTeam; }
    public void setWinningTeam(String winningTeam) { this.winningTeam = winningTeam; }

    public List<GameResultPlayer> getGameResultPlayers() { return gameResultPlayers; }

    public void addGameResultPlayer(GameResultPlayer gameResultPlayer) { this.gameResultPlayers.add(gameResultPlayer); }
}
