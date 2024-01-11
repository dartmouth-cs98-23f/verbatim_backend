package com.cs98.VerbatimBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardChallengeResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private StandardChallenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String responseQ1;

    private String responseQ2;

    private String responseQ3;

    private String responseQ4;

    private String responseQ5;




}
