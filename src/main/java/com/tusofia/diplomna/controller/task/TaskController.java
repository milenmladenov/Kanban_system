package com.tusofia.diplomna.controller.task;

import com.tusofia.diplomna.dto.CommentDTO;
import com.tusofia.diplomna.dto.TaskDTO;
import com.tusofia.diplomna.model.Comment;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.TaskRepository;
import com.tusofia.diplomna.service.comment.CommentService;
import com.tusofia.diplomna.service.plan.PlanService;
import com.tusofia.diplomna.service.task.TaskService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@Controller
public class TaskController {
  @Autowired private UserService userService;

  @Autowired private TaskService taskService;

  @Autowired private TaskRepository taskRepository;

  @Autowired private PlanService planService;

  @Autowired private CommentService commentService;

  public User getLoggedUser() {
    return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @GetMapping("/task-list")
  public String tasklist(HttpServletRequest req, Model model, Long id) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("task", new Task());
    // Get uncompleted tasks and sort by date
    List<Task> taskList = taskService.findByUserAndCompletedIsFalseAndApprovedIsTrue(userLogged);
    taskList.sort(Comparator.comparing(Task::getTargetDate));
    // get completed tasks and sort by date
    List<Task> completedTasksList =
        taskService.findByUserAndCompletedIsTrueAndApprovedIsTrue(userLogged);
    completedTasksList.sort(Comparator.comparing(Task::getTargetDate));

    // Check for pending tasks
    List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
    model.addAttribute("pendingTasks", pendingTasks);
    model.addAttribute("pendingTasksCount", pendingTasks.size());

    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("tasks", taskList);
      model.addAttribute("completedTasks", completedTasksList);
      userService.updateUserAttributes(userLogged, req);
    }
    String today = new Date(Calendar.getInstance().getTime().getTime()).toString();
    model.addAttribute("today", today);
    return "task-list";
  }

  @GetMapping("/tasks-pending")
  public String getPendingTasksPage(Model model, Authentication authentication) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("loggedUser", userLogged);
    List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
    model.addAttribute("pendingTasks", pendingTasks);
    return "tasks-pending";
  }

  @PostMapping("/task-list")
  public String newTask(Model model, @RequestParam("targetDate") String date, TaskDTO taskDTO) {
    User userLogged = getLoggedUser();
    List<Task> taskList = taskService.findByUser(userLogged);
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("tasks", taskList);
    }

    taskService.save(taskDTO, userLogged.getId());
    userService.incrementTasksCreated(userLogged);
    return "redirect:/task-list";
  }

  @DeleteMapping("/task-list")
  public String deleteTaskFromList(@RequestParam Long id, Authentication authentication) {
    User userLogged = getLoggedUser();
    // Check if it's user's task
    if (taskService.findByUser(getLoggedUser()).contains(taskService.getOne(id))) {
      taskService.deleteTaskById(id);
      userService.decrementTasksCreated(userLogged);
      if (taskService.getById(id).isCompleted()) {
        userService.decrementTasksCompleted(userLogged);
      }
      return "redirect:/task-list?deleted";
    } else {
      return "redirect:/task-list?notfound";
    }
  }

  /**
   * This function is used to assign a task to a user
   *
   * @param model The model is an object that contains the data that will be displayed on the view.
   * @param taskDTO This is a DTO object that will be used to transfer data from the frontend to the backend.
   * @param id the id of the task to be assigned
   * @param userId the id of the user to assign the task to
   * @return A String
   */
  @GetMapping("/task-assign")
  public String assignTaskPage(Model model, TaskDTO taskDTO, @RequestParam Long id, Long userId) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    Task task = taskService.getById(id);
    Plan plan = task.getPlan();
    List<User> membersPlansList = userService.findAllByPlans(plan);
    List<Task> notAssignedTasks = taskRepository.findByPlanAndAssignedIsFalse(plan);
    if (task != null) {
      model.addAttribute("task", task);
      model.addAttribute("plan", plan);
      model.addAttribute("notAssignedTasks", notAssignedTasks);
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("members", membersPlansList);
      return "task-assign";
    }
    return "redirect:task-list?usernotfound";
  }

  /**
   * The function assigns a task to a user
   *
   * @param model the model object that will be passed to the view
   * @param id the id of the task
   * @param userId the id of the user to whom the task is assigned
   * @param taskDTO the task that is being assigned
   * @return A redirect to the profile page of the user that was assigned the task.
   */
  @PostMapping("/task-assign")
  public String assignTask(Model model, Long id, @RequestParam Long userId, TaskDTO taskDTO) {
    User userLogged = getLoggedUser();
    Task task = taskService.getById(taskDTO.getId());
    User assignedTo = userService.getById(userId);
    model.addAttribute("loggedUser", userLogged);
    task.setAssigned(true);
    task.setUser(assignedTo);
    taskService.assignTo(assignedTo, task);
    return "redirect:/profile?id=" + assignedTo.getId() + "&assigned";
  }

  @GetMapping("/task-approve")
  public String approveTask(
      Authentication authentication, @RequestParam Long id, HttpServletRequest req) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
    req.setAttribute("pendingTasksCount", pendingTasks.size());
    if (id != null) {
      if (taskService.findByUser(userLogged).contains(taskService.getOne(id))) {

        taskService.approveTask(id);

        req.getSession().setAttribute("pendingTasksCount", pendingTasks.size() - 1);
        return "redirect:/tasks-pending?approved";
      } else {
        return "redirect:/tasks-pending?notfound";
      }
    } else {
      return "redirect:/tasks-pending?notfound";
    }
  }

  @GetMapping("/task-deny")
  public String denyTask(@RequestParam Long id) {
    Task task = taskService.getById(id);
    User userLogged = getLoggedUser();
    if (id != null) {
      if (taskService.findByUser(userLogged).contains(taskService.getOne(id))) {
        taskService.denyTask(task, task.getId());
        return "redirect:/tasks-pending?denied";
      } else {
        return "redirect:/tasks-pending?notfound";
      }
    } else {
      return "redirect:/tasks-pending?notfound";
    }
  }

  @GetMapping("/task-delete")
  public String deleteTask(@RequestParam Long id, Authentication authentication) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    // Check if it's user's task
    if (taskService.findByUser(getLoggedUser()).contains(taskService.getOne(id))) {
      taskService.deleteTaskById(id);
      userService.decrementTasksCreated(userLogged);
      if (taskService.getById(id).isCompleted()) {
        userService.decrementTasksCompleted(userLogged);
      }
      return "redirect:/task-list?deleted";
    } else {
      return "redirect:/task-list?notfound";
    }
  }

  @GetMapping("/task-edit")
  public String editTaskOnScreen(Model model, @RequestParam Long id) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<Task> taskList = taskService.findByUser(userLogged);
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("tasks", taskList);
    }
    // Check if it's user's task
    if (taskService.findByUser(getLoggedUser()).contains(taskService.getOne(id))) {
      model.addAttribute("task", taskService.getOne(id));
      return "/task-edit";
    } else {
      return "redirect:task-list?notfound";
    }
  }

  @PutMapping("/task-edit")
  public String editTask(Model model, Task task) {
    User userLogged = getLoggedUser();
    List<Task> taskList = taskService.findByUser(userLogged);
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("tasks", taskList);
    }
    taskService.editById(
        task.getId(), task.getDescription(), task.getTargetDate(), task.isCompleted());
    return "redirect:task-list?updated";
  }

  @GetMapping("/task-complete")
  public String completeTask(@RequestParam Long id, Authentication authentication) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    taskService.completeTaskById(id);
    // Update statistics and rank
    userService.incrementTasksCompleted(userLogged);
    return "redirect:/task-list";
  }

  @PostMapping("/task-complete")
  public String completeTask(@RequestParam Long id) {
    User userLogged = getLoggedUser();
    taskService.completeTaskById(id);
    userService.incrementTasksCompleted(userLogged);
    return "redirect:/task-list";
  }

  @GetMapping("/task-uncomplete")
  public String uncompleteTask(@RequestParam Long id, Authentication authentication) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    taskService.uncompleteTaskById(id);
    userService.decrementTasksCompleted(userLogged);
    return "redirect:/task-list";
  }

  /**
   * This function is used to display the task page
   *
   * @param taskId the id of the task that we want to display
   * @param model The model is a Map<String, Object> that is used to pass values from the controller
   *     to the view.
   * @return A String
   */
  @GetMapping("/task")
  public String singleTaskPage(@RequestParam Long taskId, Model model) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    Task currentTask = taskService.getById(taskId);
    List<Comment> commentList = commentService.findByTask(currentTask);
    Plan plan = currentTask.getPlan();
    if (currentTask != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("task", currentTask);
      model.addAttribute("comments", commentList);
      model.addAttribute("comment", new Comment());
      model.addAttribute("byId", Comparator.comparing(Comment::getId).reversed());
      model.addAttribute("plan", plan);
    }
    return "task";
  }

  @PostMapping("/task")
  public String addComment(
      @RequestParam(required = false) Long taskId,
      Model model,
      CommentDTO commentDto,
      TaskDTO taskDTO) {
    User userLogged = getLoggedUser();
    Task currentTask = taskService.getById(taskId);
    if (currentTask != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("comment", new Comment());
    }
    commentDto.setTask(currentTask);
    commentService.save(commentDto, currentTask.getId());
    return "redirect:/task?taskId=" + currentTask.getId();
  }

  @PutMapping("/task")
  public String changeStatus(
      @RequestParam(required = false) Long taskId, Model model, TaskDTO taskDTO) {
    Task currentTask = taskService.getById(taskId);
    model.addAttribute("task", currentTask);
    currentTask.setStatus(taskDTO.getStatus());
    taskService.changeStatus(currentTask.getId());
    return "redirect:/task?taskId=" + currentTask.getId();
  }
}
