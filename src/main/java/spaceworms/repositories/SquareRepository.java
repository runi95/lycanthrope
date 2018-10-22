package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.Square;

public interface SquareRepository extends CrudRepository<Square, Integer> {

}
