package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.Plan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  @NotEmpty private String username;

  @NotEmpty private String firstName;

  @NotEmpty private String lastName;

  @NotEmpty private String password;

  @NotEmpty private String confirmPassword;

  @Email @NotEmpty private String email;

  @Email @NotEmpty private String confirmEmail;

  private Date registrationDate;

  private Plan plan;

  private boolean isActive;
}
