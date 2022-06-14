package com.tusofia.diplomna.controller.authentication;

import com.tusofia.diplomna.dto.UserDto;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model, Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            model.addAttribute("page-title", "Login");
            return "login";
        } else {
            return "redirect:/";
        }

    }

    @PostMapping("/login")
    public String login(Model model, HttpServletResponse response, UserDto user){
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
