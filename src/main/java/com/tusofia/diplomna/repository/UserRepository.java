package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);

  User findByUsername(String username);

  User getById(Long id);

  List<User> findAllByPlans(Plan plan);

  User findByResetPasswordToken(String token);

  @Query("SELECT u FROM User u WHERE u.fullName  LIKE %?1%")
  List<User> searchByFullNameLikeAndPlansIs(@Param("fullName") String name, Plan plan);

  @Query("SELECT u FROM User u WHERE u.fullName LIKE %?1%")
  List<User> searchByFullNameLike(@Param("fullName") String name);
  }

