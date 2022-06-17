package com.tusofia.diplomna.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String status;

    @OneToMany(orphanRemoval=true, cascade=CascadeType.ALL)
    private List<Task> task;

   @OneToOne
    private User creator;

   @ManyToOne
    private User member;



}
