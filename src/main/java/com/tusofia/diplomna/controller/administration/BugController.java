package com.tusofia.diplomna.controller.administration;

import com.tusofia.diplomna.config.CustomAppSettings;
import com.tusofia.diplomna.model.Bug;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.bug.BugService;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
public class BugController {
    @Autowired
    private UserService userService;

    @Autowired
    private BugService bugService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/report-bug")
    public String reportBugPage(Authentication authentication, Model model, HttpServletRequest req) {
        User userLogged = userService.findByUser(authentication.getName());
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("bug", new Bug());
            userService.updateUserAttributes(userLogged, req);
        }
        return "/report-bug";
    }
    /**
    * POST controller for submitting a new bug
    */
    @PostMapping("/report-bug")
    public String reportBugSubmit(Authentication authentication, Bug bug) {
        User userLogged = userService.findByUser(authentication.getName());
        if (userLogged.getBugsReported() < CustomAppSettings.MAXIMUM_BUG_REPORTS) {
            bugService.save(new Bug(userLogged, bug.getDescription(), new Date(Calendar.getInstance().getTime().getTime())));
            userService.addBugReport(userLogged);
            bugService.save(new Bug(userLogged, bug.getDescription(), new Date(Calendar.getInstance().getTime().getTime())));
            return "redirect:/report-bug?reported";
        } else {
            return "redirect:/report-bug?failed";
        }

    }

    @GetMapping("/bug-fix")
    public String fixBug(Authentication authentication, @RequestParam("id") Long id) {
        User userLogged = userService.findByUser(authentication.getName());
        Bug bug = bugService.findById(id);
        bug.setFixed(true);
        bugService.save(bug);
        return "redirect:admin/bugreports";
    }

    @GetMapping("/bug-unfix")
    public String unfixBug(Authentication authentication, @RequestParam("id") Long id) {
        User userLogged = userService.findByUser(authentication.getName());
        Bug bug = bugService.findById(id);
        bug.setFixed(false);
        bugService.save(bug);
        return "redirect:admin/bugreports";
    }

    @GetMapping("/admin/bugreports")
    public String getBugReports(Model model, Authentication authentication, @RequestParam(value = "view", required = false) Long param) {
        User userLogged = userService.findByUser(authentication.getName());
        // Get buglist
        List<Bug> bugs = bugService.findByFixedIsFalse();
        List<Bug> fixedBugs = bugService.findByFixedIsTrue();
        // Sort
        bugs.sort(Comparator.comparing(Bug::getId).reversed());
        fixedBugs.sort(Comparator.comparing(Bug::getId).reversed());
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            if (param == null) {
                model.addAttribute("bugs", bugs);
            } else if (param == 1) {
                model.addAttribute("fixedBugs", fixedBugs);
            } else {
                return "redirect:/bugreports";
            }
        }
        return "bugreports";
    }

    @GetMapping("/admin/bug-notify")
    public String notifyReporter(Authentication authentication, @RequestParam("id") Long id) {
        User userLogged = userService.findByUser(authentication.getName());
        Bug bug = bugService.findById(id);
        messageService.bugNotifyMessage(userLogged, bug.getUser(), bug);
        return "redirect:/admin/bugreports?notified";
    }
}
