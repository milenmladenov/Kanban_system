package com.tusofia.diplomna.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty private String title;

  @NotEmpty private String description;

  @NotNull private Date targetDate;

  private boolean completed;

  private boolean approved;

  private boolean assigned;

  private String status;

  @ManyToOne private User user;

  @ManyToOne private User creator;

  @ManyToOne private Plan plan;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  List<Comment> comments;
}
