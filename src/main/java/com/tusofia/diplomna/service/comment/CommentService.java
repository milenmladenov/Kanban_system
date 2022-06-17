package com.tusofia.diplomna.service.comment;

import com.tusofia.diplomna.dto.AddCommentDto;
import com.tusofia.diplomna.model.Comment;
import com.tusofia.diplomna.model.Task;

import java.util.List;

public interface CommentService {

    void save(AddCommentDto addCommentDto, Long taskId);
    List<Comment> findByTask(Task task);
}
