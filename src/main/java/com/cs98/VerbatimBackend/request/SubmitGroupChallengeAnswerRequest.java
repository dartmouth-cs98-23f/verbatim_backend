package com.cs98.VerbatimBackend.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitGroupChallengeAnswerRequest {
    private String username;

    private int challengeId;

    private List<String> responses;
}
