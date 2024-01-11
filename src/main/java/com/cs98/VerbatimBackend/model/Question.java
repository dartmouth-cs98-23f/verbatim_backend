package com.cs98.VerbatimBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "q1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ1;

    @OneToMany(mappedBy = "q2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ2;

    @OneToMany(mappedBy = "q3", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ3;

    @OneToMany(mappedBy = "q1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ1;

    @OneToMany(mappedBy = "q2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ2;

    @OneToMany(mappedBy = "q3", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ3;

    @OneToMany(mappedBy = "q4", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ4;

    @OneToMany(mappedBy = "q5", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ5;
}
