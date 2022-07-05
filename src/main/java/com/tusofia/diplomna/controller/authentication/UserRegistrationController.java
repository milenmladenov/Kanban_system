package com.tusofia.diplomna.controller.authentication;

import com.tusofia.diplomna.dto.UserDTO;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {
  @Autowired private UserService userService;

  @ModelAttribute("user")
  public UserDTO userRegistrationDto() {
    return new UserDTO();
  }

  /**
   * If the user is not logged in, show the registration form. Otherwise, redirect to the home page
   *
   * @param authentication The Authentication object that represents the user that is currently logged in.
   * @return A string that is the name of the view to be rendered.
   */
  @GetMapping
  public String showRegistrationForm(Authentication authentication) {
    if (authentication == null) {
      return "registration";
    } else {
      return "redirect:/";
    }
  }

  // A method that is called when the user submits the registration form. It checks if the user already exists in the
  // database and if not, it saves the user.
  @PostMapping
  public String registerUserAccount(
      @ModelAttribute("user") @Valid UserDTO userDto, BindingResult result) {

    User existing_username = userService.findByUser(userDto.getUsername());
    if (existing_username != null) {
      result.rejectValue(
          "username", null, "There is already an account registered with that username");
    }

    User existingEmail = userService.findByEmail(userDto.getEmail());
    if (existingEmail != null) {
      result.rejectValue("email", null, "There is already an account registered with that email");
    }

    if (result.hasErrors()) {
      return "registration";
    }
    userService.save(userDto);
    return "redirect:/registration?success";
  }
}
