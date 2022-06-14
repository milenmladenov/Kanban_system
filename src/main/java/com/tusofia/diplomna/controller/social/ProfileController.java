package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;



@Controller
public class ProfileController {
    @Autowired
    private UserService userService;



    @GetMapping("/profile")
    public String root(HttpServletRequest req, Model model, @RequestParam(required = false) Long id) {
        User userLogged = userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("loggedUser", userLogged);
        if (userLogged != null) {
            userService.updateUserAttributes(userLogged, req);
        }
        if (id != null) {
            User paramUser = userService.getById(id);
            if (paramUser != null) {
                model.addAttribute("paramUser", paramUser);
            } else {
                return "redirect:/profile?id="+userLogged.getId();
            }
        } else {
            return "redirect:/profile?id="+userLogged.getId();
        }
        return "profile";
    }
}
