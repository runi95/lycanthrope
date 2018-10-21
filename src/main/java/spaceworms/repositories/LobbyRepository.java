package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.Lobby;

public interface LobbyRepository extends CrudRepository<Lobby, Integer> {

}