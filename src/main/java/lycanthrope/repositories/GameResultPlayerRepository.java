package lycanthrope.repositories;

import lycanthrope.models.GameResultPlayer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameResultPlayerRepository extends CrudRepository<GameResultPlayer, Long>{
    List<GameResultPlayer> findAll();
}
