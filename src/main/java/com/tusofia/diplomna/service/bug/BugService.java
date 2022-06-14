package com.tusofia.diplomna.service.bug;


import com.tusofia.diplomna.model.Bug;

import java.util.List;

public interface BugService {
    List<Bug> findAll();
    Bug save(Bug bug);
    List<Bug> findByFixedIsFalse();
    Bug findById(Long id);
    List<Bug> findByFixedIsTrue();
}
