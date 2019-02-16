package lycanthrope.services;

import lycanthrope.models.GameResultPlayer;
import lycanthrope.repositories.GameResultPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameResultPlayerService {

    @Autowired
    private GameResultPlayerRepository gameResultPlayerRepository;

    public List<GameResultPlayer> findAll() {
        return gameResultPlayerRepository.findAll();
    }

    public Optional<GameResultPlayer> find(long id) {
        return gameResultPlayerRepository.findById(id);
    }

    public void save(GameResultPlayer gameResultPlayer) {
        gameResultPlayerRepository.save(gameResultPlayer);
    }
}
