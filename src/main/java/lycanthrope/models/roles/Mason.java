package lycanthrope.models.roles;

import lycanthrope.models.*;

public class Mason implements PlayerRole {

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
        return "Mason";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) {return new NightAction[]{new NightAction() {
        @Override
        public boolean isAbleToTargetNeutrals() {
            return false;
        }

        @Override
        public boolean isAbleToTargetOtherPlayers() {
            return false;
        }

        @Override
        public boolean isAbleToTargetSelf() {
            return false;
        }

        @Override
        public boolean isAbleToViewCertainRoles() { return true; }

        @Override
        public Roles[] getViewableRoles() {
            return new Roles[]{Roles.MASON1, Roles.MASON2};
        }
    }};}
}
