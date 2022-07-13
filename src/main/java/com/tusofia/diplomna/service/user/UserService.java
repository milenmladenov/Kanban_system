package com.tusofia.diplomna.service.user;

import com.tusofia.diplomna.dto.UserDTO;
import com.tusofia.diplomna.exception.UserNotFoundException;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends UserDetailsService {
  User findByEmail(String email);

  User save(UserDTO registration);

  User findByUser(String user);

  boolean authenticate(String username, String password);

  List<User> findAll();

  void addBugReport(User userLogged);

  public User editByUser(
      User user, String firstName, String lastName, String email, String username);

  void incrementMessagesReceivedStats(User user);

  void incrementMessagesSentStats(User user);

  User setSocialSettings(User user, String facebook, String twitter, String skype, String github);

  User setName(User user, String firstName, String lastName);

  void incrementTasksCreated(User user);

  void decrementTasksCreated(User user);

  void incrementTasksReceived(User user);

  void updateUserAttributes(User user, HttpServletRequest req);

  User addMember(Plan plan, User user);

  User removeMember(Plan plan, User user);

  void incrementTasksCompleted(User user);

  void decrementTasksCompleted(User user);

  List<User> findAllByPlans(Plan plan,String fullName);

  User setShowEmail(User user, boolean value);

  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

  User getById(Long id);

  List<User> searchByFirstNameAndLastNameLike(String name);

  void updateResetPasswordToken(String token, String email) throws UserNotFoundException;

  User getByResetPasswordToken(String token);

  void updatePassword(User user, String newPassword);
}
