package com.tusofia.diplomna.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    private User user;

    private Date targetDate;

    private boolean completed;

    private boolean approved;

    private boolean assigned;

    private String status;

    @ManyToOne
    private User creator;

    @ManyToOne
    private Plan plan;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    List<Comment> comments;


    public Task(User userLogged, String description, Date targetDate, boolean completed, User userLogged1, boolean b) {
    }
}
