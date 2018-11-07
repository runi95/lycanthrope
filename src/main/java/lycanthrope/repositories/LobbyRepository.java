package lycanthrope.repositories;

import org.springframework.data.repository.CrudRepository;
import lycanthrope.models.Lobby;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

}