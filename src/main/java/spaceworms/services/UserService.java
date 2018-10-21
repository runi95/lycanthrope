package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.User;
import spaceworms.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public boolean saveUser(User user) {
        if (findByNickname(user.getNickname()).isPresent()) {
            return false;
        }

        userRepository.save(user);
        return true;
    }
}
