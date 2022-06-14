package com.tusofia.diplomna.controller;

import com.tusofia.diplomna.dto.PlanCreationDto;
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

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
public class MainController {

    private UserService userService;
    private PlanService planService;


    public MainController(@Lazy UserService userService,@Lazy PlanService planService) {
        this.userService = userService;
        this.planService = planService;
    }

    public User getLoggedUser(){
        return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/")
    public String root(HttpServletResponse response, Model model,Long id) {
        User userLogged = getLoggedUser();
        List<Plan> creatorList = planService.findByCreator(userLogged);
        List<Plan> memberList = planService.findByMember(userLogged);
            if(userLogged == null){
            return "redirect:/login";

        }
            model.addAttribute("plan", new Plan());
            model.addAttribute("createdPlans",creatorList);
            model.addAttribute("memberPlans",memberList);
            model.addAttribute("loggedUser",userLogged);
            model.addAttribute("view","index");
            return "index";
    }

    @PostMapping("/")
    public String addNewPlan(Model model, PlanCreationDto planCreationDto) {
        User userLogged = getLoggedUser();
        List<User> users = userService.findAll();
        model.addAttribute("plan", new Plan());
        model.addAttribute("loggedUser", userLogged);
        model.addAttribute("users", users);
        planService.save(planCreationDto, userLogged.getId());
        return "redirect:/";

    }


}
