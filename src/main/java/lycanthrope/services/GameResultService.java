package lycanthrope.services;

import lycanthrope.models.GameResult;
import lycanthrope.repositories.GameResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameResultService {

    @Autowired
    private GameResultRepository gameResultRepository;

    public void save(GameResult gameResult) {
        gameResultRepository.save(gameResult);
    }

    public Optional<GameResult> find(long id) {
        return gameResultRepository.findById(id);
    }

    public List<GameResult> findAll() {
        return gameResultRepository.findAll();
    }
}
