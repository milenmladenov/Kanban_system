package com.tusofia.diplomna.service.task;

import com.tusofia.diplomna.dto.TaskCreationDto;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;


public interface TaskService {
    List<Task> findByUser(User user);

    Task findById(int id);

    Task getById(Long id);
    void deleteTaskById(Long id);
    Task completeTaskById(Long id);
    Task uncompleteTaskById(Long id);
    void changeStatus(Long id);
    void deleteTaskById(int id);
    Task assignTo(User user,Task task);
    Task save(TaskCreationDto taskCreationDto,Long id);
    Task getOne(Long id);
    Task editById(Long id, String desc, Date date, boolean completed);
    List<Task> findByUserAndCompletedIsFalse(User user);
    List<Task> findByUserAndCompletedIsTrue(User user);
    String getMotivationalMessage(List<Task> taskList, User user);
    List<Task> findByUserAndCompletedIsFalseAndApprovedIsTrue(User user);
    List<Task> findByUserAndCompletedIsTrueAndApprovedIsTrue(User user);
    List<Task> findByPlanAndAssignedIsFalse(Plan plan);
    List<Task> findByUserAndApprovedIsFalse(User user);

    void approveTask(Long id);
    void denyTask(Task task,Long id);
    void updateAttributes(User user, HttpServletRequest req);
    List<Task> findByPlanAndStatusIs(Plan plan,String status);


    Task uncompleteTaskById(int id);
    String modifyDateLayouts(String targetDate) throws ParseException;
}
