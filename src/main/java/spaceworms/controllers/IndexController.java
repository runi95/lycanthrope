package spaceworms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spaceworms.authentication.UserPrincipal;
import spaceworms.models.User;
import spaceworms.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

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

        boolean isUserSaved = userService.save(user);
        if (isUserSaved) {
            UserPrincipal userPrincipal = new UserPrincipal(user);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/boards";
        } else {
            model.addAttribute("error", "That nickname is already taken!");

            return "index";
        }
    }
}
