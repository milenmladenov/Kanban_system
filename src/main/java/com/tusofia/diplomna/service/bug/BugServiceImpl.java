package com.tusofia.diplomna.service.bug;


import com.tusofia.diplomna.model.Bug;
import com.tusofia.diplomna.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BugServiceImpl implements BugService {
    @Autowired
    public BugRepository bugRepository;

    @Override
    public List<Bug> findAll() {
        return bugRepository.findAll();
    }

    @Override
    public Bug save(Bug bug) {
        return bugRepository.save(bug);
    }


    @Override
    public List<Bug> findByFixedIsFalse() {
        return bugRepository.findByFixedIsFalse();
    }

    @Override
    public Bug findById(Long id) {
        return bugRepository.getById(id);
    }

    @Override
    public List<Bug> findByFixedIsTrue() {
        return bugRepository.findByFixedIsTrue();
    }
}
