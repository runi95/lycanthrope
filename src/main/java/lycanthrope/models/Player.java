package lycanthrope.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonBackReference
    private User user;

    private int votesAgainstPlayer;

    private String vote;

    @JsonIgnore
    private int roleId;

    @JsonIgnore
    private int actionsPerformed;

    @JsonIgnore
    private String nightActionTarget;

    @JsonIgnore
    private String nightActionTargetTwo;

    @JsonIgnore
    private String dayAction;

    @JsonIgnore
    private boolean isRealInsomniac;

    /*
    @JsonIgnore
    private Long doppelgangerTargetId;
    */

    public int getId() { return id; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId ) { this.roleId = roleId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getVotesAgainstPlayer() { return votesAgainstPlayer; }
    public void setVotesAgainstPlayer(int votesAgainstPlayer) { this.votesAgainstPlayer = votesAgainstPlayer; }

    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }

    public int getActionsPerformed() { return actionsPerformed; }
    public void setActionsPerformed(int actionsPerformed) { this.actionsPerformed = actionsPerformed; }

    public String getNightActionTarget() { return nightActionTarget; }
    public void setNightActionTarget(String nightActionTarget) { this.nightActionTarget = nightActionTarget; }

    public String getNightActionTargetTwo() { return nightActionTargetTwo; }
    public void setNightActionTargetTwo(String nightActionTargetTwo) { this.nightActionTargetTwo = nightActionTargetTwo; }

    public String getDayAction() { return dayAction; }
    public void setDayAction(String dayAction) { this.dayAction = dayAction; }

    public boolean isRealInsomniac() { return isRealInsomniac; }
    public void setRealInsomniac(boolean realInsomniac) { isRealInsomniac = realInsomniac; }

    /*
    public Long getDoppelgangerTargetId() { return doppelgangerTargetId; }
    public void setDoppelgangerTargetId(Long doppelgangerTargetId) { this.doppelgangerTargetId = doppelgangerTargetId; }
    */
}