package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
  List<Plan> findByCreator(User user);

  List<Plan> findByMembers(User user);

  Plan findByTaskId(Long id);

  @Query("SELECT p FROM Plan p WHERE p.title LIKE %?1%")
  List<Plan> searchByTitleLikeAndMembersIs(@Param("title") String title,User member);
  @Query("SELECT p FROM Plan p WHERE p.title LIKE %?1%")
  List<Plan> searchByTitleLikeAndCreatorIs(@Param("title") String title,User creator);
}
