package com.tusofia.diplomna.service.user;

import com.tusofia.diplomna.dto.UserDTO;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Role;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.UserRepository;
import com.tusofia.diplomna.service.message.MessageService;
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
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;
  private TaskService taskService;
  private MessageService messageService;
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  @Lazy
  public UserServiceImpl(
      UserRepository userRepository,
      TaskService taskService,
      MessageService messageService,
      BCryptPasswordEncoder passwordEncoder) {
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

  /**
   * We create a new user object, set the fields, encode the password, and save the user
   *
   * @param registration The UserDTO object that contains the user's registration information.
   * @return A User object
   */
  public User save(UserDTO registration) {
    User user = new User();
    user.setUsername(registration.getUsername());
    user.setFirstName(registration.getFirstName());
    user.setLastName(registration.getLastName());
    user.setEmail(registration.getEmail());
    user.setPassword(passwordEncoder.encode(registration.getPassword()));
    user.setRegistrationDate(new Date(Calendar.getInstance().getTime().getTime()));
    return userRepository.save(user);
  }

  /**
   * > The function `findByUser` takes a string as an argument and returns a user object
   *
   * @param user The username of the user to find.
   * @return A user object
   */
  @Override
  public User findByUser(String user) {
    return userRepository.findByUsername(user);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public void addBugReport(User userLogged) {
    userRepository.save(userLogged);
  }


  @Override
  public User editByUser(
      User user, String firstName, String lastName, String email, String username) {
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setUsername(username);
    return userRepository.save(user);
  }

  @Override
  public void incrementMessagesReceivedStats(User user) {
    user.setMessagesReceived(user.getMessagesReceived() + 1);
    userRepository.save(user);
  }

  @Override
  public void incrementMessagesSentStats(User user) {
    user.setMessagesSent(user.getMessagesSent() + 1);
    userRepository.save(user);
  }

  @Override
  public User setSocialSettings(
      User user, String facebook, String twitter, String skype, String github) {
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
    user.setTasksMade(user.getTasksMade() + 1);
    userRepository.save(user);
  }

  @Override
  public void decrementTasksCreated(User user) {
    user.setTasksMade(user.getTasksMade() - 1);
    userRepository.save(user);
  }

  @Override
  public void incrementTasksReceived(User user) {
    User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    userLogged.setTasksReceived(userLogged.getTasksReceived() + 1);
    userRepository.save(userLogged);
  }

  @Override
  public void updateUserAttributes(User user, HttpServletRequest req) {
    taskService.updateAttributes(user, req);
    messageService.updateAttributes(user, req);
  }

  @Override
  public void incrementTasksCompleted(User user) {
    User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    user.setTasksCompleted(userLogged.getTasksCompleted() + 1);
    userRepository.save(user);
  }

  @Override
  public void decrementTasksCompleted(User user) {
    User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    user.setTasksCompleted(userLogged.getTasksCompleted() - 1);
    userRepository.save(user);
  }

  @Override
  public List<User> findAllByPlans(Plan plan) {
    return userRepository.findAllByPlans(plan);
  }

  /**
   * It adds a member to a plan.
   *
   * @param plan The plan to which the user is added.
   * @param user The user to be added to the plan.
   * @return User
   */
  @Override
  public User addMember(Plan plan, User user) {
    User userLogged = findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    // Creating a new message object, setting the fields, and saving the message.
    Message notifyMessage = new Message();
    notifyMessage.setReceiver(user);
    notifyMessage.setSender(userLogged);
    notifyMessage.setDate(new Date(Calendar.getInstance().getTime().getTime()));
    notifyMessage.setOpened(0);
    notifyMessage.setSubject("You have been added to new plan: " + plan.getTitle());
    notifyMessage.setMessageText(
        "<p>Hello,</p><br/>You have been added to: <blockquote>"
            + plan.getTitle()
            + "</blockquote> You can check the existing tasks !<small><em></br>This is an automated message and not written by the user self.</em></small>");
    messageService.autoreply(notifyMessage);
    user.getPlans().add(plan);
    return userRepository.save(user);
  }

  @Override
  public User setShowEmail(User user, boolean value) {
    user.setShowEmail(value);
    return userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("No user found with username: " + username);
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
  }

  /**
   * If the username and password match, return true. Otherwise, return false
   *
   * @param username The username of the user to authenticate.
   * @param password The password the user entered.
   * @return A boolean value.
   */
  @Override
  public boolean authenticate(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      return false;
    }
    String userName = user.getUsername();
    String pass = user.getPassword();
    return userName.equals(username) && pass.equals(password);
  }

  private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());
  }
}
