package lycanthrope.repositories;

import lycanthrope.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
}
