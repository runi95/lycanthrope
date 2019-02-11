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

    @JsonIgnore
    private int roleId;

    @JsonIgnore
    private int actionsPerformed;

    @JsonIgnore
    private String nightActionTarget;

    @JsonIgnore
    private String nightActionTargetTwo;

    /*
    @JsonIgnore
    private Long doppelgangerTargetId;
    */

    public int getId() { return id; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId ) { this.roleId = roleId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getActionsPerformed() { return actionsPerformed; }
    public void setActionsPerformed(int actionsPerformed) { this.actionsPerformed = actionsPerformed; }

    public String getNightActionTarget() { return nightActionTarget; }
    public void setNightActionTarget(String nightActionTarget) { this.nightActionTarget = nightActionTarget; }

    public String getNightActionTargetTwo() { return nightActionTargetTwo; }
    public void setNightActionTargetTwo(String nightActionTargetTwo) { this.nightActionTargetTwo = nightActionTargetTwo; }

    /*
    public Long getDoppelgangerTargetId() { return doppelgangerTargetId; }
    public void setDoppelgangerTargetId(Long doppelgangerTargetId) { this.doppelgangerTargetId = doppelgangerTargetId; }
    */
}