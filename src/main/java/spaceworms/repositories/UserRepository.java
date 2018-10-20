package spaceworms.repositories;

import org.springframework.data.repository.CrudRepository;
import spaceworms.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByNickname(String nickname);
}
