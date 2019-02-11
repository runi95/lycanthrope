package lycanthrope.services;

import lycanthrope.models.PlayerRole;
import lycanthrope.models.roles.*;
import org.springframework.stereotype.Service;

@Service
public class PlayerRoleService {

    private final PlayerRole[] roles = new PlayerRole[]{
            new Hunter(), // 0
            new Drunk(), // 1
            new Insomniac(), // 2
            new Troublemaker(), // 3
            new Robber(), // 4
            new Seer(), // 5
            new Mason(), // 6
            new Mason(), // 7
            new Tanner(), // 8
            new Minion(), // 9
            new Doppelganger(), // 10
            new Villager(), // 11
            new Villager(), // 12
            new Villager(), // 13
            new Werewolf(), // 14
            new Werewolf() // 15
    };

    public PlayerRole getRole(int roleId) {
        if (roleId < 0 || roleId > roles.length) {
            return null;
        }

        return roles[roleId];
    }
}
