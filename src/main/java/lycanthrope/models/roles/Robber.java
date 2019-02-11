package lycanthrope.models.roles;

import lycanthrope.models.*;

public class Robber implements PlayerRole {

    int robbedPlayerId;

    @Override
    public boolean isAbleToPerformOnDeathAction() {
        return false;
    }

    @Override
    public Team getTeam() {
        return Team.Village;
    }

    @Override
    public String getName() {
        return "Robber";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) { return new NightAction[]{new NightAction() {
        @Override
        public boolean isAbleToTargetNeutrals() {
            return false;
        }

        @Override
        public boolean isAbleToTargetOtherPlayers() {
            return true;
        }

        @Override
        public boolean isAbleToTargetSelf() {
            return false;
        }

        @Override
        public boolean isAbleToViewCertainRoles() { return false; }

        @Override
        public Roles[] getViewableRoles() {
            return new Roles[]{};
        }
    }}; }

    public int getRobbedPlayerId() {
        return robbedPlayerId;
    }

    public void setRobbedPlayerId(int robbedPlayerId) {
        this.robbedPlayerId = robbedPlayerId;
    }
}
