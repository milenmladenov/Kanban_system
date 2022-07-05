package com.tusofia.diplomna.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String username;
  private String firstName;
  private String lastName;
  @Email @JsonIgnore private String email;
  @JsonIgnore private String password;
  private int bugsReported;
  private int tasksMade;
  private int tasksCompleted;
  private int tasksReceived;
  private int tasksAssigned;
  private int messagesReceived;
  private int messagesSent;
  private Date registrationDate;
  private String country;
  private int age;
  private boolean showEmail = true;
  private boolean isEnabled;
  // Social
  private String skype;
  private String twitter;
  private String github;
  private String facebook;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Collection<Role> roles;

  @OneToMany private List<Task> tasks;

  @OneToMany(cascade = CascadeType.ALL)
  private List<Message> messages;

  @OneToMany(cascade = CascadeType.ALL)
  private List<Comment> comments;

  @OneToOne private Plan createdPlan;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "member_plans",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "plan_id", referencedColumnName = "id"))
  private List<Plan> plans;

  public User(User member) {}
  //    public User(String username, String password,String email, Collection<? extends
  // GrantedAuthority> mapRolesToAuthorities) {
  //    }
}
