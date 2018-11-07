package lycanthrope.repositories;

import org.springframework.data.repository.CrudRepository;
import lycanthrope.models.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
}
