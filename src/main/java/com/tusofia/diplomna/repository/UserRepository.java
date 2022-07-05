package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);

  User findByUsername(String username);

  User getById(Long id);

  List<User> findAllByPlans(Plan plan);
}
