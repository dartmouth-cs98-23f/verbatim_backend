package com.cs98.VerbatimBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "q1_id")
    private Question q1;

    @ManyToOne
    @JoinColumn(name = "q2_id")
    private Question q2;

    @ManyToOne
    @JoinColumn(name = "q3_id")
    private Question q3;

    @ManyToOne
    @JoinColumn(name = "q4_id")
    private Question q4;

    @ManyToOne
    @JoinColumn(name = "q5_id")
    private Question q5;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private GroupChallenge challenge;

    @JsonIgnore
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardChallengeResponse> responses;
}
