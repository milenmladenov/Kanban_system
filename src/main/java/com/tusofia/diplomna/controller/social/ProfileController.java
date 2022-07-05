package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
  @Autowired private UserService userService;


  /**
   * If the user is logged in, then show the profile page. If the user is not logged in, then redirect to the login page
   *
   * @param model The model is a Map that is used to store the data that will be displayed on the view page.
   * @param id The id of the user whose profile you want to view.
   * @return A String
   */
  @GetMapping("/profile")
  public String root(Model model, @RequestParam(required = false) Long id) {
    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    model.addAttribute("loggedUser", userLogged);
    if (userLogged == null) {
      return "redirect:/login";
    }
    // If the id is not null, then get the user by id and add it to the model. If the user is null, then redirect to the
    // profile page of the logged user. If the id is null, then redirect to the profile page of the logged user.
    if (id != null) {
      User paramUser = userService.getById(id);
      if (paramUser != null) {
        model.addAttribute("paramUser", paramUser);
      } else {
        return "redirect:/profile?id=" + userLogged.getId();
      }
    } else {
      return "redirect:/profile?id=" + userLogged.getId();
    }
    return "profile";
  }
}
