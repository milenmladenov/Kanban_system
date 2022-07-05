package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.MembersPlans;
import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembersPlansRepository extends JpaRepository<MembersPlans, Long> {
  List<MembersPlans> getAllPlanByMember(User member);

  List<MembersPlans> getAllMemberByPlan(Plan plan);

  MembersPlans findMemberByPlan(Plan plan);

  List<MembersPlans> findByPlan(Plan plan);
}
