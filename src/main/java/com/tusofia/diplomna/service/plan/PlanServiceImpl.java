package com.tusofia.diplomna.service.plan;

import com.tusofia.diplomna.dto.PlanDTO;
import com.tusofia.diplomna.model.MembersPlans;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.MembersPlansRepository;
import com.tusofia.diplomna.repository.PlanRepository;
import com.tusofia.diplomna.repository.UserRepository;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

  private PlanRepository planRepository;
  private UserService userService;
  private MembersPlansRepository membersPlansRepository;
  private MessageService messageService;
  private UserRepository userRepository;

  @Autowired
  @Lazy
  public PlanServiceImpl(
      PlanRepository planRepository,
      UserService userService,
      MembersPlansRepository membersPlansRepository,
      MessageService messageService) {
    this.planRepository = planRepository;
    this.userService = userService;
    this.membersPlansRepository = membersPlansRepository;
    this.messageService = messageService;
  }

  @Override
  public Plan getById(Long id) {
    return planRepository.getById(id);
  }

  @Override
  public void deletePlanById(Long id) {
    planRepository.deleteById(id);
  }

  /**
   * The function takes a PlanDTO object and a Long id as parameters, and returns a Plan object
   *
   * @param planDto This is the object that will be sent from the frontend.
   * @param id The id of the plan to be updated.
   * @return A Plan object
   */
  @Override
  public Plan save(PlanDTO planDto, Long id) {
    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    Plan plan = new Plan();
    plan.setCreator(userLogged);
    plan.setTitle(planDto.getTitle());
    return planRepository.save(plan);
  }

  @Override
  public List<Plan> getAll() {
    return planRepository.findAll();
  }

  @Override
  public List<Plan> findByCreator(User user) {
    return planRepository.findByCreator(user);
  }

  @Override
  public List<MembersPlans> memberList(Plan plan) {
    return membersPlansRepository.getAllMemberByPlan(plan);
  }

  @Override
  public List<Plan> findByMember(User member) {
    return planRepository.findAllByMembers(member);
  }

  @Override
  public Plan findPlanByTaskId(Long id) {
    return planRepository.findByTaskId(id);
  }

  @Override
  public List<MembersPlans> findByPlan(Plan plan) {
    return membersPlansRepository.findByPlan(plan);
  }

  @Override
  public void removeMember(User user, Long id) {
    User planMember = userService.getById(id);
  }

  @Override
  public List<MembersPlans> getMemberById(Long id) {
    return null;
  }
}
