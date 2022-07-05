package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

  @NotNull private Long id;

  @NotEmpty private String title;

  @NotEmpty private User creator;

  @NotEmpty private User member;
}
