package lycanthrope.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextImpl;
import lycanthrope.authentication.UserPrincipal;
import lycanthrope.services.UserService;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Configuration
public class HttpSessionConfig {

    private Logger logger = LoggerFactory.getLogger(HttpSessionConfig.class);

    @Autowired
    UserService userService;

    @Bean                           // bean for http session listener
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                // We won't do anything here for now
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                if (se.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null) {
                    SecurityContextImpl securityContext = (SecurityContextImpl) se.getSession().getAttribute("SPRING_SECURITY_CONTEXT");

                    if (securityContext.getAuthentication().getPrincipal() != null && securityContext.getAuthentication().getPrincipal() instanceof UserPrincipal) {
                        UserPrincipal principal = (UserPrincipal) securityContext.getAuthentication().getPrincipal();

                        userService.deleteUserByNickname(principal.getUsername());
                    }
                }
            }
        };
    }

}
