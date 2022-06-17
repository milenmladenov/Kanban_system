package com.tusofia.diplomna.service.plan;

import com.tusofia.diplomna.dto.PlanCreationDto;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.PlanRepository;
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

    @Autowired
    @Lazy
    public PlanServiceImpl(PlanRepository planRepository, UserService userService) {
        this.planRepository = planRepository;
        this.userService = userService;
    }

    @Override
    public Plan getById(Long id) {
        return planRepository.getById(id);
    }

    @Override
    public void deletePlanById(Long id) {
        planRepository.deleteById(id);
    }

    @Override
    public Plan save(PlanCreationDto planCreationDto, Long id) {
        User userLogged = userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
        Plan plan = new Plan();
        plan.setCreator(userLogged);
        plan.setTitle(planCreationDto.getTitle());
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
    public List<Plan> findByCreatorAndMember(User creator, User member) {
        return planRepository.findByCreatorAndMember(creator,member);

    }

    @Override
    public List<Plan> findByMember(User member) {
        return planRepository.findByMember(member);
    }


    @Override
    public Plan findPlanByTaskId(Long id) {
        return planRepository.findByTaskId(id);
    }


//    @Override
//    public void addMember(AddMemberToPlanDto member){
//        User newMember = new User();
//        newMember.
//            }

    @Override
    public void removeMember(User user, Long id) {
        User planMember = userService.getById(id);
        planRepository.deleteMemberById(planMember.getId());
    }
}


