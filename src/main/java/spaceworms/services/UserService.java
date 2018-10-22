package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spaceworms.models.User;
import spaceworms.repositories.UserRepository;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Pattern regexPattern = Pattern.compile("^[\\w\\-']+$");

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public boolean save(User user) {
        if (findByNickname(user.getNickname()).isPresent()) {
            return false;
        }

        // User's nickname didn't match our naming criteria
        if (user.getNickname().length() > 16 || !regexPattern.matcher(user.getNickname()).matches()) {
            return false;
        }

        userRepository.save(user);
        return true;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
