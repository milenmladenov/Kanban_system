package com.tusofia.diplomna.controller.authentication;

import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.user.UserService;
import com.tusofia.diplomna.dto.UserDto;
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
    @Autowired
    private UserService userService;

    @ModelAttribute("user")
    public UserDto userRegistrationDto() {
        return new UserDto();
    }

    @GetMapping
    public String showRegistrationForm(Authentication authentication) {
        if (authentication == null) {
            return "registration";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                      BindingResult result) {

        User existing_username = userService.findByUser(userDto.getUsername());
        if (existing_username != null) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }

        User existing_email = userService.findByEmail(userDto.getEmail());
        if (existing_email != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            return "registration";
        }
        userService.save(userDto);
        return "redirect:/registration?success";
    }

}
