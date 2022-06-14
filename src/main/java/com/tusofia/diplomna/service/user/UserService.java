package com.tusofia.diplomna.service.user;

import com.tusofia.diplomna.dto.AddMemberToPlanDto;
import com.tusofia.diplomna.dto.UserDto;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


public interface UserService extends UserDetailsService {
    User findByEmail(String email);
    User save(UserDto registration);
    User findByUser(String user);
    Optional<User> findById(Long id);

    boolean authenticate(String username, String password);

    List<User> findAll();
    void addBugReport(User userLogged);
    User editByUser(User user, String firstName, String lastName, String country, int age, String facebook, String skype,
                    String github, String twitter, String email, String username);
    void incrementMessagesReceivedStats(User user);
    void incrementMessagesSentStats(User user);
    User setSocialSettings(User user, String facebook, String twitter, String skype, String github);
    User setName(User user, String firstName, String lastName);
    void incrementTasksCreated(User user);
    void decrementTasksCreated(User user);

    void incrementTasksReceived(User user);
    void incrementTasksAssigned(User user,Long id);

    void updateUserAttributes(User user, HttpServletRequest req);

    void incrementTasksCompleted(User user);
    void decrementTasksCompleted(User user);

    User setMotivationalTaskMessage(User user, boolean value);
    User setSmallCalendar(User user, boolean value);
    User setTodoToCalendar(User user, boolean value);
    User setShowEmail(User user, boolean value);
    List <User> getAllMembers(Plan plan);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    void addMemberToPlan(AddMemberToPlanDto addMemberToPlanDto);
    User getById(Long id);
}
