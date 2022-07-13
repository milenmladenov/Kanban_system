package com.tusofia.diplomna.service.task;

import com.tusofia.diplomna.dto.TaskDTO;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.TaskRepository;
import com.tusofia.diplomna.repository.UserRepository;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

  private TaskRepository taskRepository;
  private MessageService messageService;
  private UserService userService;
  private UserRepository userRepository;

  @Autowired
  @Lazy
  public TaskServiceImpl(
      TaskRepository taskRepository,
      MessageService messageService,
      UserService userService,
      UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.messageService = messageService;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @Override
  public List<Task> findByUser(User user) {
    return taskRepository.findByUser(user);
  }

  @Override
  public Task getById(Long id) {
    return taskRepository.getById(id);
  }

  @Override
  public void deleteTaskById(Long id) {
    taskRepository.deleteById(id);
  }

  @Override
  public Task completeTaskById(Long id) {
    Task task = taskRepository.getById(id);
    task.setCompleted(true);
    return taskRepository.save(task);
  }

  @Override
  public Task uncompleteTaskById(Long id) {
    return null;
  }

  @Override
  public void changeStatus(Task task) {
    taskRepository.save(task);
  }

  /**
   * The function takes in a TaskDTO object and a Long id, and returns a Task object
   *
   * @param taskDTO This is the object that is passed from the frontend.
   * @param id The id of the task to be updated.
   * @return A task object is being returned.
   */
  @Override
  public Task save(TaskDTO taskDTO, Long id) {
    Task task = new Task();
    task.setCreator(taskDTO.getCreator());
    task.setUser(taskDTO.getUser());
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setTargetDate(taskDTO.getTargetDate());
    task.setPlan(taskDTO.getPlan());
    task.setStatus("NOT STARTED");
    return taskRepository.save(task);
  }

  @Override
  public Task getOne(Long id) {
    return taskRepository.getById(id);
  }

  @Override
  public Task editById(Long id, String desc, Date date, boolean completed) {
    Task task = taskRepository.getById(id);
    task.setDescription(desc);
    task.setTargetDate(date);
    task.setCompleted(task.isCompleted());
    return taskRepository.save(task);
  }

  @Override
  public String getMotivationalMessage(List<Task> taskList, User user) {

    if (taskList.size() == 0 && taskRepository.findByUserAndCompletedIsTrue(user).size() == 0) {
      return "Hmm... What about giving yourself some tasks?";
    } else if (taskList.size() == 0
        && taskRepository.findByUserAndCompletedIsTrue(user).size() > 0) {
      return "Good job! You've completed all your tasks! Be proud about yourself.";
    } else if (taskList.size() == 1) {
      return "What, only 1 task? You should finish your work!";
    } else if (taskList.size() > 1 && taskList.size() <= 5) {
      return "You only have "
          + taskList.size()
          + " tasks... That's not that much. Go complete them!";
    } else if (taskList.size() > 5 && taskList.size() <= 10) {
      return "You've got some work there, "
          + taskList.size()
          + " tasks to complete. Try to complete as much as possible today.";
    } else if (taskList.size() > 10) {
      return taskList.size()
          + " tasks! Let's see how many you could complete today! Proof yourself!";
    } else {
      return "Error";
    }
  }

  @Override
  public List<Task> findByUserAndCompletedIsFalseAndApprovedIsTrue(User user) {
    return taskRepository.findByUserAndCompletedIsFalseAndApprovedIsTrue(user);
  }

  @Override
  public List<Task> findByUserAndCompletedIsTrueAndApprovedIsTrue(User user) {
    return taskRepository.findByUserAndCompletedIsTrueAndApprovedIsTrue(user);
  }

  @Override
  public List<Task> findByUserAndApprovedIsFalse(User user) {
    return taskRepository.findByUserAndApprovedIsFalse(user);
  }

  @Override
  public void approveTask(Long id) {
    Task task = taskRepository.getById(id);
    task.setApproved(true);
    // Increment stats for assign/received
    User user = userService.getById(task.getUser().getId());
    userService.incrementTasksReceived(user);
    User creatorUser = userService.getById(task.getCreator().getId());
    creatorUser.setTasksAssigned(user.getTasksAssigned() + 1);
    userRepository.save(user);
    //         Increment the user (who got the task)'s tasksCreated
    userService.incrementTasksCreated(user);
    taskRepository.save(task);
  }

  @Override
  public void denyTask(Task task, Long id) {
    taskRepository.getById(id);
    deleteTaskById(task.getId());
    Message notifyMessage = new Message();
    notifyMessage.setReceiver(task.getCreator());
    notifyMessage.setSender(task.getUser());
    notifyMessage.setDate(new Date(Calendar.getInstance().getTime().getTime()));
    notifyMessage.setOpened(0);
    notifyMessage.setSubject(
        "Your assigned task to " + task.getUser().getUsername() + " has been denied.");
    notifyMessage.setMessageText(
        "<p>Hello "
            + task.getCreator().getUsername()
            + ",</p><br/>You have assigned the following task to me: <blockquote>"
            + task.getDescription()
            + "</blockquote><p>I hereby wish to inform you that I have denied your task.</p><small><em>This is an automated message and not written by the user self.</em></small>");
    messageService.autoreply(notifyMessage);
  }

  @Override
  public void updateAttributes(User user, HttpServletRequest req) {
    List<Task> taskList = taskRepository.findByUserAndCompletedIsFalseAndApprovedIsTrue(user);
    req.getSession().setAttribute("tasksLeft", taskList.size());
    String motivationMessage = getMotivationalMessage(taskList, user);
    req.getSession().setAttribute("taskMotivation", motivationMessage);
    List<Task> pendingTasks = findByUserAndApprovedIsFalse(user);
    req.getSession().setAttribute("pendingTasksCount", pendingTasks.size());
  }

  @Override
  public List<Task> findByPlanAndStatusIs(Plan plan, String status) {
    return taskRepository.findByPlanAndStatusIs(plan, status);
  }

  @Override
  public List<Task> findByUserAndStatusIs(User user,String status) {
    return taskRepository.findByUserAndStatusIs(user,status);
  }

  @Override
  public Task assignTo(User user, Task task) {
    task.setApproved(true);
    // Increment stats for assign/received
    userService.incrementTasksReceived(user);
    User creatorUser = userService.getById(task.getCreator().getId());
    creatorUser.setTasksAssigned(user.getTasksAssigned() + 1);
    userRepository.save(user);
    //         Increment the user (who got the task)'s tasksCreated
    userService.incrementTasksCreated(user);
    task.setUser(user);
    return taskRepository.save(task);
  }
}
