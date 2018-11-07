package lycanthrope.repositories;

import org.springframework.data.repository.CrudRepository;
import lycanthrope.models.Square;

public interface SquareRepository extends CrudRepository<Square, Integer> {

}
