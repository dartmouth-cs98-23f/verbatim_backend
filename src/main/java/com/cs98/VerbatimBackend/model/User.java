package com.cs98.VerbatimBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.Where;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String profilePicture;

    private String bio;

    private Integer numGlobalChallengesCompleted;

    private Integer numCustomChallengesCompleted;

    private Integer streak;


}
