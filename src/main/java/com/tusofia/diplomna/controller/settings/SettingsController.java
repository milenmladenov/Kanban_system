package com.tusofia.diplomna.controller.settings;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.UserRepository;
import com.tusofia.diplomna.service.user.UserService;
import com.tusofia.diplomna.utility.NameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SettingsController {
  @Autowired UserService userService;

  @Autowired UserRepository userRepository;

  private String REDIRECT_ERROR = "redirect:/settings?error";

  @GetMapping("/settings")
  public String getSettings(Model model, Authentication authentication, HttpServletRequest req) {
    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("loggedUser", userLogged);
    userService.updateUserAttributes(userLogged, req);

    return "settings";
  }

  @PostMapping("/settings")
  public String setSettings(User user, Authentication authentication) {

    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    if (userLogged != null) {
      if (user.getFirstName().length() >= 2
          && user.getFirstName().length() <= 20
          && user.getLastName().length() > 2
          && user.getLastName().length() <= 40)
        if (user.getFirstName().trim().chars().allMatch(Character::isLetter)
            && NameValidator.check(user.getLastName())) {
          userService.setName(userLogged, user.getFirstName(), user.getLastName());
        }
    }
    userRepository.save(userLogged);
    return "redirect:/settings?success";
  }
}
