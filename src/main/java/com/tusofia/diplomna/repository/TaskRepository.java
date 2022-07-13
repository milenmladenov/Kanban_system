package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUser(User user);

  List<Task> findByUserAndCompletedIsTrue(User user);

  List<Task> findByPlanAndAssignedIsFalse(Plan plan);

  List<Task> findByUserAndCompletedIsFalseAndApprovedIsTrue(User user);

  List<Task> findByUserAndCompletedIsTrueAndApprovedIsTrue(User user);

  List<Task> findByUserAndApprovedIsFalse(User user);

  List<Task> findByPlanAndStatusIs(Plan plan, String status);

  List<Task> findByUserAndStatusIs(User user,String status);

  Task getById(Long id);

  void deleteById(Long id);
}
