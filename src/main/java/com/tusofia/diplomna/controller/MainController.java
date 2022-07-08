package com.tusofia.diplomna.controller;

import com.tusofia.diplomna.dto.PlanDTO;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.plan.PlanService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainController {

  private UserService userService;
  private PlanService planService;

  public MainController(@Lazy UserService userService, @Lazy PlanService planService) {
    this.userService = userService;
    this.planService = planService;
  }

  public User getLoggedUser() {
    return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  /**
   * The function returns the index page, which is the main page of the application. It also adds the plans created by the
   * logged in user, the plans the logged in user is a member of, and the logged in user to the model
   *
   * @param model The model is a Map that is used to pass values from the controller to the view.
   * @return The index page is being returned.
   */
  @GetMapping("/")
  public String planList(Model model) {
    User userLogged = getLoggedUser();
    List<Plan> creatorList = planService.findByCreator(userLogged);
    List<Plan> memberList = planService.findByMember(userLogged);
    System.out.println(memberList);
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("plan", new Plan());
    model.addAttribute("createdPlans", creatorList);
    model.addAttribute("memberPlans", memberList);
    model.addAttribute("loggedUser", userLogged);
    model.addAttribute("view", "index");
    return "index";
  }

  /**
   * It takes a PlanDTO object, gets the logged user, gets all users, adds the PlanDTO object to the model, adds the logged
   * user to the model, adds all users to the model, and saves the PlanDTO object to the database
   *
   * @param model The model is a Map that is used to store the data that will be displayed on the view page.
   * @param planDto the object that will be sent to the frontend.
   * @return A redirect to the root path.
   */
  @PostMapping("/")
  public String addNewPlan(Model model, PlanDTO planDto) {
    User userLogged = getLoggedUser();
    model.addAttribute("plan", new Plan());
    model.addAttribute("loggedUser", userLogged);
    planService.save(planDto, userLogged.getId());
    return "redirect:/";
  }
}
