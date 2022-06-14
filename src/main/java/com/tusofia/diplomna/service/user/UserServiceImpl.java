package com.tusofia.diplomna.service.user;

import com.tusofia.diplomna.dto.AddMemberToPlanDto;
import com.tusofia.diplomna.dto.UserDto;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Role;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.UserRepository;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.plan.PlanService;
import com.tusofia.diplomna.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private TaskService taskService;
    private MessageService messageService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserServiceImpl(UserRepository userRepository, TaskService taskService, MessageService messageService, BCryptPasswordEncoder passwordEncoder, PlanService planService) {
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.messageService = messageService;
        this.passwordEncoder = passwordEncoder;

    }



    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    public User save(UserDto registration) {
        User user = new User();
        user.setUsername(registration.getUsername());
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRegistrationDate(new Date(Calendar.getInstance().getTime().getTime()));
        return userRepository.save(user);
    }

    @Override
    public User findByUser(String user) {
        return userRepository.findByUsername(user);
    }


    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void addBugReport(User userLogged) {
        userLogged.setBugsReported(userLogged.getBugsReported()+1);
        userRepository.save(userLogged);
    }

    @Override
    public User editByUser(User user, String firstName, String lastName, String country, int age, String facebook, String skype, String github, String twitter, String email, String username) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCountry(country);
        user.setAge(age);
        user.setFacebook(facebook);
        user.setSkype(skype);
        user.setGithub(github);
        user.setTwitter(twitter);
        user.setEmail(email);
        user.setUsername(username);
        return userRepository.save(user);
    }


    @Override
    public void incrementMessagesReceivedStats(User user) {
        user.setMessagesReceived(user.getMessagesReceived()+1);
        userRepository.save(user);
    }

    @Override
    public void incrementMessagesSentStats(User user) {
        user.setMessagesSent(user.getMessagesSent()+1);
        userRepository.save(user);
    }

    @Override
    public User setSocialSettings(User user, String facebook, String twitter, String skype, String github) {
        user.setFacebook(facebook);
        user.setTwitter(twitter);
        user.setSkype(skype);
        user.setGithub(github);
        return userRepository.save(user);
    }

    @Override
    public User setName(User user, String firstName, String lastName) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Override
    public void incrementTasksCreated(User user) {
        user.setTasksMade(user.getTasksMade()+1);
        userRepository.save(user);
    }

    @Override
    public void decrementTasksCreated(User user) {
        user.setTasksMade(user.getTasksMade()-1);
        userRepository.save(user);
    }

    @Override
    public void incrementTasksReceived(User user) {
        User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        userLogged.setTasksReceived(userLogged.getTasksReceived()+1);
        userRepository.save(userLogged);
    }

    @Override
    public void incrementTasksAssigned(User user, Long id) {
        User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        userRepository.findById(id);
        user.setId(userLogged.getId());
        user.setTasksAssigned(userLogged.getTasksAssigned()+1);
        userRepository.save(user);
    }

    @Override
    public void updateUserAttributes(User user, HttpServletRequest req) {
        taskService.updateAttributes(user, req);
        messageService.updateAttributes(user, req);
    }

    @Override
    public void incrementTasksCompleted(User user) {
        User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setTasksCompleted(userLogged.getTasksCompleted()+1);
        userRepository.save(user);
    }

    @Override
    public void decrementTasksCompleted(User user) {
        User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setTasksCompleted(userLogged.getTasksCompleted()-1);
        userRepository.save(user);
    }

    @Override
    public User setMotivationalTaskMessage(User user, boolean value) {
        user.setMotivationalTaskMessage(value);
        return userRepository.save(user);
    }

    @Override
    public User setSmallCalendar(User user, boolean value) {
        user.setSmallCalendar(value);
        return userRepository.save(user);
    }

    @Override
    public User setTodoToCalendar(User user, boolean value) {
        user.setTodoToCalendar(value);
        return userRepository.save(user);
    }

    @Override
    public User setShowEmail(User user, boolean value) {
        user.setShowEmail(value);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllMembers(Plan plan) {
        return userRepository.findByPlan(plan);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(
                    "No user found with username: "+ username);
        }

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;


        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    @Override
    public void addMemberToPlan(AddMemberToPlanDto addMemberToPlanDto) {

    }


    @Override
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            return false;
        }
        String u_name = user.getUsername();
        String pass = user.getPassword();
        return u_name.equals(username) && pass.equals(password);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
