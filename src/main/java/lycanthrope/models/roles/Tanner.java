package lycanthrope.models.roles;

import lycanthrope.models.Lobby;
import lycanthrope.models.NightAction;
import lycanthrope.models.PlayerRole;
import lycanthrope.models.Team;

@SuppressWarnings("Duplicates")
public class Tanner implements PlayerRole {

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
