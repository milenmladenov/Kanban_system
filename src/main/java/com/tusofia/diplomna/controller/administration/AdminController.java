package com.tusofia.diplomna.controller.administration;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.image.StorageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AdminController {
  @Autowired private UserService userService;

  @Autowired private StorageService storageService;

  @GetMapping("/admin")
  public String getAdminPanel(Model model, Authentication authentication, HttpServletRequest req) {
    User userLogged = userService.findByUser(authentication.getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    if (userLogged.getRoles().contains("ROLE_ADMIN")) {
      model.addAttribute("loggedUser", userLogged);
      userService.updateUserAttributes(userLogged, req);
    }

    return "admin";
  }

  @GetMapping("/admin/manageusers")
  public String getUserManagement(Model model, Authentication authentication) {
    User userLogged = userService.findByUser(authentication.getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<User> users = userService.findAll();

    model.addAttribute("loggedUser", userLogged);
    model.addAttribute("users", users);

    return "manageusers";
  }

  @GetMapping("/admin/manageusers/{id}")
  public String getUserManagement(
      Model model, Authentication authentication, @PathVariable Long id) {
    User userLogged = userService.findByUser(authentication.getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("loggedUser", userLogged);
    if (id != null) {
      User paramUser = userService.getById(id);
      model.addAttribute("paramUser", paramUser);
    }

    return "edituser";
  }

  @PostMapping("/admin/manageusers/{id}")
  public String editUser(User user, @PathVariable Long id) {
    User paramUser = userService.getById(id);
    userService.editByUser(
        paramUser, user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    return "redirect:/admin/manageusers/" + paramUser.getId();
  }

  @GetMapping("/admin/manageusers/{id}/delete-avatar")
  public String deleteUserAvatar(@PathVariable Long id) {
    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    User paramUser = userService.getById(id);
    if (paramUser != null) {
      storageService.deleteById(id);
      return "redirect:/admin/manageusers/" + id;
    }
    return "redirect:/admin/manageusers/" + id;
  }
}
