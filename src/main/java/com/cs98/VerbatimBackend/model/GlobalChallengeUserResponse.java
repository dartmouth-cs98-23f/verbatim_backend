package com.cs98.VerbatimBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "global_challenge_response")
public class GlobalChallengeUserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "global_challenge_id")
    private GlobalChallenge globalChallenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String responseQ1;

    private String responseQ2;

    private String responseQ3;

    private String responseQ4;

    private String responseQ5;

}
