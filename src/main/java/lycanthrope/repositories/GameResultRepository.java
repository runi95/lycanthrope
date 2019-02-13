package lycanthrope.repositories;

import lycanthrope.models.GameResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameResultRepository extends CrudRepository<GameResult, Long> {
    List<GameResult> findAll();
}
