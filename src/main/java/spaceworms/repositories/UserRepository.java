package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
}
