package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ListUsersController {
  @Autowired private UserService userService;

  public User getLoggedUser(Authentication authentication) {
    return userService.findByUser(authentication.getName());
  }

  @GetMapping("/users")
  public String listUsers(HttpServletRequest req, Model model, Authentication authentication, @Param("fullName") String fullName) {
    User userLogged = getLoggedUser(authentication);
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<User> users = userService.searchByFirstNameAndLastNameLike(fullName);
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("users", users);
    }
    return "users";
  }
}
