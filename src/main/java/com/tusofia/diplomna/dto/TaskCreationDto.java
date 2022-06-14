package com.tusofia.diplomna.dto;


import com.tusofia.diplomna.model.Plan;
import com.tusofia.diplomna.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationDto {

    @NotEmpty
    private String description;

    @NotEmpty
    private User user;

    @NotNull
    private Date targetDate;

    private String status;

    private Plan plan;
}
