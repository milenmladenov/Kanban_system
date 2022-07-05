package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
  List<Plan> findByCreator(User user);

  List<Plan> findAllByMembers(User user);

  Plan findByTaskId(Long id);
}
