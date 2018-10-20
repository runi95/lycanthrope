package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}