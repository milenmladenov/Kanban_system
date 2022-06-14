package com.tusofia.diplomna.controller.task;

import com.tusofia.diplomna.dto.TaskCreationDto;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.TaskRepository;
import com.tusofia.diplomna.service.plan.PlanService;
import com.tusofia.diplomna.service.task.TaskService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PlanService planService;

    public User getLoggedUser() {
        return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/task-list")
    public String tasklist(HttpServletRequest req, Model model,Long id) {
        User userLogged = getLoggedUser();
            model.addAttribute("task", new Task());
            // Get uncompleted tasks and sort by date
            List<Task> taskList = taskService.findByUserAndCompletedIsFalseAndApprovedIsTrue(userLogged);
            taskList.sort(Comparator.comparing(Task::getTargetDate));
            // get completed tasks and sort by date
            List<Task> completedTasksList = taskService.findByUserAndCompletedIsTrueAndApprovedIsTrue(userLogged);
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
        model.addAttribute("loggedUser", userLogged);
        List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
        model.addAttribute("pendingTasks", pendingTasks);
        return "tasks-pending";
    }

    @PostMapping("/task-list")
    public String newTask(Model model,@RequestParam("targetDate") String date,TaskCreationDto taskCreationDto) {
        User userLogged = getLoggedUser();
        List<Task> taskList = taskService.findByUser(userLogged);
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("tasks", taskList);
        }

        taskService.save(taskCreationDto,userLogged.getId());
        userService.incrementTasksCreated(userLogged);
        return "redirect:/task-list";
    }

    @GetMapping("/task-assign")
    public String assignTaskPage(Model model, TaskCreationDto taskCreationDto, @RequestParam Long id) {
        User userLogged = getLoggedUser();
        User recepient = userService.getById(id);
        if (recepient != null) {
            if (userLogged.getId() != id) {
                model.addAttribute("recepient",recepient);
                model.addAttribute("loggedUser", userLogged);
                model.addAttribute("task", new TaskCreationDto());
                taskCreationDto.setUser(recepient);
                return "task-assign";
            } else {
                return "redirect:task-list?self";
            }
        } else {
            return "redirect:task-list?usernotfound";
        }
    }

    @PostMapping("/task-assign")
    public String assignTask(Model model, Task task, Authentication authentication, @RequestParam Long id, @RequestParam("targetDate") String date,TaskCreationDto taskCreationDto) {
        User userLogged = getLoggedUser();
        User assignedTo = userService.getById(id);
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
        }
        if (assignedTo != null) {
            taskService.save(taskCreationDto,assignedTo.getId());
            return "redirect:/profile?id="+assignedTo.getId()+"&assigned";
        } else {
            return "redirect:task-list?usernotfound";
        }
    }

    @GetMapping("/task-approve")
    public String approveTask(Authentication authentication, @RequestParam Long id, HttpServletRequest req) {
        User userLogged = getLoggedUser();
        List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
        req.setAttribute("pendingTasksCount", pendingTasks.size());
        if (id != null) {
            if (taskService.findByUser(userLogged).contains(taskService.getOne(id))) {

                taskService.approveTask(id);

                    req.getSession().setAttribute("pendingTasksCount", pendingTasks.size()-1);
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
                taskService.denyTask(task,task.getId());
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
    public String editTask(Model model, Task task, Authentication authentication) {
        User userLogged = getLoggedUser();
        List<Task> taskList = taskService.findByUser(userLogged);
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("tasks", taskList);
        }
        taskService.editById(task.getId(),task.getDescription(),task.getTargetDate(), task.isCompleted());
        return "redirect:task-list?updated";
    }

    @GetMapping("/task-complete")
    public String completeTask(@RequestParam Long id, Authentication authentication) {
        User userLogged = getLoggedUser();
        taskService.completeTaskById(id);
        // Update statistics and rank
        userService.incrementTasksCompleted(userLogged);
        return "redirect:/task-list";
    }

    @PostMapping("/task-complete")
    public String completeTask(@RequestParam Long id){
        User userLogged = getLoggedUser();
        taskService.completeTaskById(id);
        userService.incrementTasksCompleted(userLogged);
        return "redirect:/task-list";

    }

    @GetMapping("/task-uncomplete")
    public String uncompleteTask(@RequestParam Long id, Authentication authentication) {
        User userLogged = getLoggedUser();
        taskService.uncompleteTaskById(id);
        userService.decrementTasksCompleted(userLogged);
        return "redirect:/task-list";
    }

}
