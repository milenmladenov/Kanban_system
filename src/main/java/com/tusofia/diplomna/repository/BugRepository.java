package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findAll();
    List<Bug> findByFixedIsFalse();
    List<Bug> findByFixedIsTrue();
}
