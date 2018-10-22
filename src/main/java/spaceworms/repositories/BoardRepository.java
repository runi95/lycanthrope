package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.Board;

public interface BoardRepository extends CrudRepository<Board, Integer> {
    
}