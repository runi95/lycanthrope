package lycanthrope.repositories;

import org.springframework.data.repository.CrudRepository;
import lycanthrope.models.Lobby;

import java.util.List;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

    List<Lobby> findAll();
}