package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreationDto {

    @NotEmpty
    private String title;

    @NotEmpty
    private User creator;

    private User member;

}
