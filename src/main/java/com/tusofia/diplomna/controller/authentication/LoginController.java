package com.tusofia.diplomna.controller.authentication;

import com.tusofia.diplomna.dto.UserDTO;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * If the user is not logged in, show the login page. Otherwise, redirect to the home page
     *
     * @param model This is the model that will be passed to the view.
     * @param authentication This is the authentication object that Spring Security uses to store the currently logged in
     * user.
     * @return A string that is the name of the view to be rendered.
     */
    @GetMapping("/login")
    public String login(Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("page-title", "Login");
            return "login";
        } else {
            return "redirect:/";
        }

    }

    /**
     * If the user is authenticated, redirect to the index page, otherwise, return the login page
     *
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param user This is the model attribute that is used to bind the form data to the model.
     * @return The login page is being returned.
     */
    @PostMapping("/login")
    public String login(Model model,UserDTO user){
        if (userService.authenticate(user.getUsername(),user.getPassword())){
            model.addAttribute("view","index");
            return "redirect:/";
        }
        model.addAttribute("view","login");
        return "index";

    }

    @GetMapping("/logout")
    public String logout(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {

            /* The user is logged in :) */
            return "redirect:logout";
        }

        model.addAttribute("user", new User());
        model.addAttribute("view", "login");
        model.addAttribute("logout", true);
        return "index";
    }

}
