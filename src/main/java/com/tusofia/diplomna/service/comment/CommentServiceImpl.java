package com.tusofia.diplomna.service.comment;

import com.tusofia.diplomna.dto.CommentDTO;
import com.tusofia.diplomna.model.Comment;
import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.CommentRepository;
import com.tusofia.diplomna.service.task.TaskService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

  private UserService userService;
  private TaskService taskService;
  private CommentRepository commentRepository;

  @Autowired
  @Lazy
  public CommentServiceImpl(
      UserService userService, TaskService taskService, CommentRepository commentRepository) {
    this.userService = userService;
    this.taskService = taskService;
    this.commentRepository = commentRepository;
  }

  @Override
  public void save(CommentDTO commentDto, Long taskId) {
    User userLogged =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    Task task = taskService.getById(taskId);
    Comment comment = new Comment();
    comment.setCommenter(userLogged);
    comment.setTask(commentDto.getTask());
    comment.setBody(commentDto.getBody());
    commentRepository.save(comment);
  }

  // A method that returns a list of comments by task.
  @Override
  public List<Comment> findByTask(Task task) {
    return commentRepository.findByTask(task);
  }
}
