package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.User;
import spaceworms.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public boolean saveIfNicknameNotTaken(User user) {
        if (findByNickname(user.getNickname()).isPresent()) {
            return false;
        }

        save(user);
        return true;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void deleteUserByNickname(String nickname) {
        Optional<User> optionalUser = findByNickname(nickname);
        if (optionalUser.isPresent()) {
            deleteUser(optionalUser.get());
        }
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }
}
