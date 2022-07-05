package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
public class TaskDTO {

  @NotNull private Long id;
  @NotEmpty private String title;
  @NotEmpty private String description;

  @NotEmpty private User user;

  @NotNull private Date targetDate;

  private String status;

  private Plan plan;
}
