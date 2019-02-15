package lycanthrope.models.roles;

import lycanthrope.models.Lobby;
import lycanthrope.models.NightAction;
import lycanthrope.models.PlayerRole;
import lycanthrope.models.Team;

@SuppressWarnings("Duplicates")
public class Villager implements PlayerRole {

    @Override
    public Team getTeam() {
        return Team.Village;
    }

    @Override
    public String getName() {
        return "Villager";
    }

    @Override
    public NightAction[] getNightActions(Lobby lobby) { return new NightAction[]{}; }
}
