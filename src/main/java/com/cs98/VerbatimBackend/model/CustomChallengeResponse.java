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
public class CustomChallengeResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "group_challenge_id")
    private  GroupChallenge groupChallenge;

    @ManyToOne
    @JoinColumn(name = "custom_challenge_id")
    private CustomChallenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "custom_question_id")
    private CustomQuestion question;

    private String response;
}
