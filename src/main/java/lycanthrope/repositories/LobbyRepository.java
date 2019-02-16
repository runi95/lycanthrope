package lycanthrope.repositories;

import lycanthrope.models.Lobby;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

    List<Lobby> findAll();
}