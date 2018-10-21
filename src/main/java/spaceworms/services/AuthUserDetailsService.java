package spaceworms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import spaceworms.authentication.UserPrincipal;
import spaceworms.models.User;

import java.util.Optional;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty())
            throw new UsernameNotFoundException(username);

        Optional<User> optionalUser = userService.findByNickname(username);
        if (!optionalUser.isPresent())
            throw new UsernameNotFoundException(username);

        User user = optionalUser.get();
        user.setRole("default");

        return new UserPrincipal(user);
    }
}
