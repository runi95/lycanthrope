package lycanthrope.models.roles;

import lycanthrope.models.*;

public class Doppelganger implements PlayerRole {

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
        return "Doppelgänger";
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
        public Roles[] getViewableRoles() { return new Roles[]{}; }
    }}; }
}
