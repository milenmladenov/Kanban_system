package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Comment;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByTask(Task task);
    List<Comment> findByCommenter(User user);
}
