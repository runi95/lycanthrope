package lycanthrope.models.roles;

import lycanthrope.models.Lobby;
import lycanthrope.models.NightAction;
import lycanthrope.models.PlayerRole;
import lycanthrope.models.Team;

public class Minion implements PlayerRole {

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
        return "Minion";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) { return null; }
}
