package lycanthrope.models.roles;

import lycanthrope.models.*;

@SuppressWarnings("Duplicates")
public class Seer implements PlayerRole {

    @Override
    public Team getTeam() {
        return Team.Village;
    }

    @Override
    public String getName() {
        return "Seer";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) { return new NightAction[]{new NightAction() {
        @Override
        public boolean isAbleToTargetNeutrals() {
            return true;
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
    }, new NightAction() {
        @Override
        public boolean isAbleToTargetNeutrals() { return true; }

        @Override
        public boolean isAbleToTargetOtherPlayers() { return false; }

        @Override
        public boolean isAbleToTargetSelf() { return false; }

        @Override
        public boolean isAbleToViewCertainRoles() { return false; }

        @Override
        public Roles[] getViewableRoles() { return new Roles[]{}; }
    }}; }
}
