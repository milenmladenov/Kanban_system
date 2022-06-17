package com.tusofia.diplomna.service.plan;

import com.tusofia.diplomna.dto.PlanCreationDto;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;

import java.util.List;

public interface PlanService {

    Plan getById(Long id);
    void deletePlanById(Long id);
    Plan save(PlanCreationDto planCreationDto, Long id);
    List<Plan> getAll();
    List<Plan> findByCreator(User user);

    List<Plan> findByCreatorAndMember(User creator, User member);
    List<Plan> findByMember(User member);
    Plan findPlanByTaskId(Long id);
//    void addMember(AddMemberToPlanDto member);
    void removeMember(User user,Long id);
}
