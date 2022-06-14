package com.tusofia.diplomna.controller.plan;

import com.tusofia.diplomna.dto.TaskCreationDto;
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

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@Controller
public class PlanController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    public User getLoggedUser() {
        return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/plan-list")
    public String planList(HttpServletRequest req, Model model) {
        User userLogged = getLoggedUser();
        List<User> users = userService.findAll();
        model.addAttribute("plan", new Plan());
        model.addAttribute("task", new Task());
        List<Task> taskList = taskService.findByUserAndCompletedIsFalseAndApprovedIsTrue(userLogged);
        taskList.sort(Comparator.comparing(Task::getTargetDate));
        List<Task> completedTasksList = taskService.findByUserAndCompletedIsTrueAndApprovedIsTrue(userLogged);
        completedTasksList.sort(Comparator.comparing(Task::getTargetDate));
        List<Task> pendingTasks = taskService.findByUserAndApprovedIsFalse(userLogged);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("pendingTasksCount", pendingTasks.size());

        if (userLogged != null) {
            model.addAttribute("users", users);
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("tasks", taskList);
            model.addAttribute("completedTasks", completedTasksList);
            userService.updateUserAttributes(userLogged, req);
        }
        String today = new Date(Calendar.getInstance().getTime().getTime()).toString();
        model.addAttribute("today", today);
        return "plan-list";
    }



    @GetMapping("/plan")
    public String getPlanById(HttpServletRequest req, Model model, @RequestParam(required = false) Long id, Long memberId) {
        User userLogged = getLoggedUser();
        List<User> users = userService.findAll();

        model.addAttribute("loggedUser", userLogged);
        if (userLogged != null) {
            userService.updateUserAttributes(userLogged, req);
        }
        if (id != null) {
            Plan plan = planService.getById(id);
            List<User> members = userService.getAllMembers(plan);
            List<Task> taskListNotStarted = taskService.findByPlanAndStatusIs(plan,"NOT STARTED");
            List<Task> taskListInProgress = taskService.findByPlanAndStatusIs(plan,"IN PROGRESS");
            List<Task> taskListDone = taskService.findByPlanAndStatusIs(plan,"DONE");

            if (plan != null) {
                model.addAttribute("members", members);
                model.addAttribute("plan", plan);
                model.addAttribute("task",new Task());
                model.addAttribute("users", users);
                model.addAttribute("notStartedTasks",taskListNotStarted);
                model.addAttribute("inProgressTasks",taskListInProgress);
                model.addAttribute("doneTasks",taskListDone);
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
        return "plan";
    }
    @PostMapping("/plan")
    public String addTask(Model model, @RequestParam(required = false) Long id, TaskCreationDto taskCreationDto){
        User userLogged = getLoggedUser();
        Plan plan = planService.getById(id);
        if (plan != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("plan", plan);
            model.addAttribute("task", new Task());
        }
        taskCreationDto.setPlan(plan);
        taskService.save(taskCreationDto,userLogged.getId());
        return "redirect:/plan?id=" + plan.getId();
    }
    @GetMapping("/plan/add-member")
    public String addMemberListPage(Model model, @RequestParam Long id) {
        User userLogged = getLoggedUser();
        List<User> users = userService.findAll();
        Plan plan = planService.getById(id);
        if (plan != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("plan", plan);
            model.addAttribute("users", users);
        } else {
            return "redirect:/plan?=" + plan.getId();
        }
        return "add-member";
    }
    @PostMapping("/plan/add-member")
    public String addMember(HttpServletRequest req, Model model,Plan plan, @RequestParam(required = false) Long memberId){
        User userLogged = getLoggedUser();
        User member = userService.getById(memberId);
        if (member != null && plan !=null){
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("member", member);
            model.addAttribute("plan", plan);
        }
        plan.setMember(member);
        planRepository.save(plan);
        model.addAttribute("successMessage","The user is added to the plan!");
        return "redirect:/plan?="+ plan.getId();
    }


    @GetMapping("/plan/add-member-confirm/")
    public String addMemberSingleMemberPage(HttpServletRequest req, Model model, @RequestParam(required = false,name="planId") Long id, @RequestParam(required = false,name ="memberId") Long memberId) {
        User userLogged = getLoggedUser();
        User member = userService.getById(memberId);
        if (member != null) {
            model.addAttribute("member", member);
        }
        model.addAttribute("loggedUser", userLogged);
        Plan plan = planService.getById(id);
        if (plan != null) {
            model.addAttribute("plan", plan);
        }
        return "add-member-confirm";
    }



}







