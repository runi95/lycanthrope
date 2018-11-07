package lycanthrope.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lycanthrope.authentication.UserPrincipal;
import lycanthrope.models.User;
import lycanthrope.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    private static final Pattern regexPattern = Pattern.compile("^[\\w\\-']+$");

    @GetMapping(value = "/")
    public String getMainPage() {
        return "index";
    }

    @PostMapping(value = "/setNick")
    public String setNickname(@RequestParam("nickname") String nickname, HttpServletRequest request, Model model) {
        // User's nickname didn't match our naming criteria
        if (nickname.length() > 16 || !regexPattern.matcher(nickname).matches()) {
            model.addAttribute("error", "That nickname uses invalid characters!");

            return "index";
        }

        User user = new User();
        user.setNickname(nickname);

        boolean isUserSaved = userService.saveIfNicknameNotTaken(user);
        if (isUserSaved) {
            UserPrincipal userPrincipal = new UserPrincipal(user);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/boards";
        } else {

            // If this log entry is spammed too much then we might want to consider removing it!
            logger.info("A user picked a nickname that was already taken");
            model.addAttribute("error", "That nickname is already taken!");

            return "index";
        }
    }
}
