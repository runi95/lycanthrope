package lycanthrope.services;

import lycanthrope.models.Player;
import lycanthrope.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public void save(Player player) {
        playerRepository.save(player);
    }
}
