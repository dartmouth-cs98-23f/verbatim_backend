package com.cs98.VerbatimBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private Integer lastGlobalChalUsed;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "q1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ1;

    @JsonIgnore
    @OneToMany(mappedBy = "q2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ2;

    @JsonIgnore
    @OneToMany(mappedBy = "q3", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ3;

    @JsonIgnore
    @OneToMany(mappedBy = "q4", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ4;

    @JsonIgnore
    @OneToMany(mappedBy = "q5", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalChallenge> globalChallengeQ5;

    @JsonIgnore
    @OneToMany(mappedBy = "q1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ1;

    @JsonIgnore
    @OneToMany(mappedBy = "q2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ2;

    @JsonIgnore
    @OneToMany(mappedBy = "q3", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ3;

    @JsonIgnore
    @OneToMany(mappedBy = "q4", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ4;

    @JsonIgnore
    @OneToMany(mappedBy = "q5", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallenge> standardChallengeQ5;
}
