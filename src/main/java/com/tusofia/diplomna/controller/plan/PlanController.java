package com.tusofia.diplomna.controller.plan;

import com.tusofia.diplomna.dto.TaskDTO;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.PlanRepository;
import com.tusofia.diplomna.repository.TaskRepository;
import com.tusofia.diplomna.service.plan.PlanService;
import com.tusofia.diplomna.service.task.TaskService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PlanController {

  @Autowired private UserService userService;

  @Autowired private PlanService planService;

  @Autowired private PlanRepository planRepository;

  @Autowired private TaskRepository taskRepository;

  @Autowired private TaskService taskService;

  /**
   * Get the currently logged in user from the database.
   *
   * @return The user object of the currently logged in user.
   */
  public User getLoggedUser() {
    return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  /**
   * This function is used to display the plan page
   *
   * @param model This is the Model object that is used to pass data to the view.
   * @param id the id of the plan
   * @return A String
   */

  @GetMapping("/plan")
  public String getPlanById(Model model, @RequestParam(required = false) Long id) {
    User userLogged = getLoggedUser();
    if(userLogged == null){
      return "redirect:/";
    }
    List<User> users = userService.findAll();
    model.addAttribute("loggedUser", userLogged);
    // This is a check if the id is not null. If it is not null, it will get the plan by id, get all the members of the
    // plan, get all the tasks that are not started, in progress and done.
    if (id != null) {
      Plan plan = planService.getById(id);
        List<User> members = userService.findAllByPlans(plan);
      List<Task> taskListNotStarted = taskService.findByPlanAndStatusIs(plan, "NOT STARTED");
      List<Task> taskListInProgress = taskService.findByPlanAndStatusIs(plan, "IN PROGRESS");
      List<Task> taskListDone = taskService.findByPlanAndStatusIs(plan, "DONE");
      // This is a check if the id is not null. If it is not null, it will get the plan by id, get all the members of the
      // plan, get all the tasks that are not started, in progress and done.
      if (plan != null) {
        model.addAttribute("members", members);
        model.addAttribute("plan", plan);
        model.addAttribute("task", new Task());
        model.addAttribute("users", users);
        model.addAttribute("notStartedTasks", taskListNotStarted);
        model.addAttribute("inProgressTasks", taskListInProgress);
        model.addAttribute("doneTasks", taskListDone);
      } else {
        return "redirect:/";
      }
    } else {
      return "redirect:/";
    }
    return "plan";
  }

  /**
   * The function adds a task to a plan
   *
   * @param model The model object is used to pass data from the controller to the view.
   * @param id the id of the plan
   * @param taskDTO the object that will be used to save the task.
   * @return A redirect to the plan page with the id of the plan.
   */
  @PostMapping("/plan")
  public String addTask(Model model, @RequestParam(required = false) Long id, TaskDTO taskDTO) {
    User userLogged = getLoggedUser();
    Plan plan = planService.getById(id);
    if (plan != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("plan", plan);
      model.addAttribute("task", new Task());
    }
    taskDTO.setCreator(userLogged);
    taskDTO.setPlan(plan);
    taskService.save(taskDTO, userLogged.getId());
    return "redirect:/plan?id=" + plan.getId();
  }

  /**
   * This function is used to add members to a plan
   *
   * @param model The model is an object that holds the data that you want to pass to the view.
   * @param planId the id of the plan that we want to add members to
   * @return A list of users that are not members of the plan.
   */
  @GetMapping("/members")
  public String addMemberListPage(Model model, @RequestParam Long planId) {
    User userLogged = getLoggedUser();
    Plan plan = planService.getById(planId);
    List<User> users = userService.findAll();
    List<User> members = userService.findAllByPlans(plan);
    users.remove(plan.getCreator());
    users.removeAll(members);
    model.addAttribute("loggedUser", userLogged);
    model.addAttribute("plan", plan);
    model.addAttribute("users", users);
    model.addAttribute("members", members);
    model.addAttribute("memberExistsMessage", "This User is already added to the plan !");
    return "members";
  }

  /**
   * The function takes in a planId and a memberId, finds the plan and member, and adds the member to the plan
   *
   * @param model The model object that will be used to render the view.
   * @param planId The id of the plan that the member is being added to.
   * @param memberId The id of the user you want to add to the plan.
   * @return A redirect to the members page with the planId as a parameter.
   */
  @PostMapping("/members")
  public String addMember(Model model, Long planId, Long memberId) {
    Plan plan = planService.getById(planId);
    User member = userService.getById(memberId);
    if (plan != null) {
      model.addAttribute("plan", plan);
      model.addAttribute("member", member);
    }
    userService.addMember(plan, member);
    return "redirect:/members?planId=" + plan.getId();
  }
}
