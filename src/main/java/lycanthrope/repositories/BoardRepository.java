package lycanthrope.repositories;

import org.springframework.data.repository.CrudRepository;
import lycanthrope.models.Board;

public interface BoardRepository extends CrudRepository<Board, Integer> {
    
}