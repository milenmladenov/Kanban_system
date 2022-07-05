package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.Task;
import com.tusofia.diplomna.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

  private User commenter;

  private String body;

  private Task task;
}
