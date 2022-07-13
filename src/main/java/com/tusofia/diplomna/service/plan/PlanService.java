package com.tusofia.diplomna.service.plan;

import com.tusofia.diplomna.dto.PlanDTO;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;

import java.util.List;

public interface PlanService {

  Plan getById(Long id);

  void deletePlanById(Long id);

  Plan save(PlanDTO planDto, Long id);

  List<Plan> getAll();

  List<Plan> findByCreator(User user,String title);

  List<Plan> findByMember(User member,String title);

  Plan findPlanByTaskId(Long id);

  void removeMember(User user, Long id);
}
