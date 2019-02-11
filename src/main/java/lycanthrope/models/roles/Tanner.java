package lycanthrope.models.roles;

import lycanthrope.models.Lobby;
import lycanthrope.models.NightAction;
import lycanthrope.models.PlayerRole;
import lycanthrope.models.Team;

public class Tanner implements PlayerRole {

    @Override
    public boolean isAbleToPerformOnDeathAction() {
        return false;
    }

    @Override
    public Team getTeam() {
        return Team.Tanner;
    }

    @Override
    public String getName() {
        return "Tanner";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) { return new NightAction[]{}; }
}
