package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spaceworms.authentication.UserPrincipal;
import spaceworms.models.User;
import spaceworms.services.UserService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/")
    public String getMainPage() {
        return "index";
    }

    // TODO: Add some sort of feedback if we fail to save the user!
    // TODO: Remove a user from DB if the user's session ends!
    @PostMapping(value = "/setNick")
    public String setNickname(@RequestParam("nickname") String nickname, HttpServletRequest request) {
        User user = new User();
        user.setNickname(nickname);

        boolean isUserSaved = userService.saveUser(user);
        if (isUserSaved) {
            UserPrincipal userPrincipal = new UserPrincipal(user);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/boards";
        } else {
            return "redirect:/";
        }
    }
}
