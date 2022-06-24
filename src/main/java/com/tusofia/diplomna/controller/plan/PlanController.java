package com.tusofia.diplomna.controller.plan;

import com.tusofia.diplomna.dto.TaskCreationDto;
import com.tusofia.diplomna.model.MembersPlans;
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
import java.util.*;

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
            List<MembersPlans> members = planService.memberList(plan);
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
    @GetMapping("/members")
    public String addMemberListPage(Model model, @RequestParam Long planId) {
        User userLogged = getLoggedUser();
        Plan plan = planService.getById(planId);
        List<User> users = userService.findAll();
        List <MembersPlans> membersPlans = planService.findByPlan(plan);
        List <User> members = userService.getMembers(membersPlans);
        users.remove(plan.getCreator());
        users.removeAll(members);
        model.addAttribute("loggedUser", userLogged);
        model.addAttribute("plan", plan);
        model.addAttribute("members", membersPlans);
        model.addAttribute("users", users);
        model.addAttribute("memberExistsMessage","This User is already added to the plan !");

        return "members";
    }

    @PostMapping("/members")
    public String addMember(Model model,Long planId, MembersPlans membersPlans,Long memberId){
        User userLogged = getLoggedUser();
        Plan plan = planService.getById(planId);
        User member = userService.getById(memberId);
        if (plan != null){
            model.addAttribute("plan", plan);
            model.addAttribute("member",member);
        }
        membersPlans.setMember(member);
        membersPlans.setPlan(plan);
        member.setMembership(membersPlans);
        planService.addMember(membersPlans);
        return "redirect:/members?planId=" + plan.getId();
    }
}







