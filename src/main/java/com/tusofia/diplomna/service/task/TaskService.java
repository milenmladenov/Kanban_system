package com.tusofia.diplomna.service.task;

import com.tusofia.diplomna.dto.TaskDTO;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.List;

public interface TaskService {
  List<Task> findByUser(User user);

  Task getById(Long id);

  void deleteTaskById(Long id);

  Task completeTaskById(Long id);

  Task uncompleteTaskById(Long id);

  void changeStatus(Task task);

  Task assignTo(User user, Task task);

  Task save(TaskDTO taskDTO, Long id);

  Task getOne(Long id);

  Task editById(Long id, String desc, Date date, boolean completed);

  String getMotivationalMessage(List<Task> taskList, User user);

  List<Task> findByUserAndCompletedIsFalseAndApprovedIsTrue(User user);

  List<Task> findByUserAndCompletedIsTrueAndApprovedIsTrue(User user);

  List<Task> findByUserAndApprovedIsFalse(User user);

  void approveTask(Long id);

  void denyTask(Task task, Long id);

  void updateAttributes(User user, HttpServletRequest req);

  List<Task> findByPlanAndStatusIs(Plan plan, String status);

  List<Task> findByUserAndStatusIs(User user,String status);
}
