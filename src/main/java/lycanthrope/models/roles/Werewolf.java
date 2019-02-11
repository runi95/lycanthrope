package lycanthrope.models.roles;

import lycanthrope.models.*;

public class Werewolf implements PlayerRole {

    @Override
    public boolean isAbleToPerformOnDeathAction() {
        return false;
    }

    @Override
    public Team getTeam() {
        return Team.Werewolf;
    }

    @Override
    public String getName() {
        return "Werewolf";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) {
        if (lobby.isLoneWolf()) {
            return new NightAction[]{new NightAction() {
                @Override
                public boolean isAbleToTargetNeutrals() {
                    return true;
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
                public boolean isAbleToViewCertainRoles() { return false; }

                @Override
                public Roles[] getViewableRoles() {
                    return new Roles[]{};
                }
            }};
        } else {
            return new NightAction[]{new NightAction() {
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
                    return new Roles[]{Roles.WEREWOLF1, Roles.WEREWOLF2};
                }
            }};
        }
    }
}
