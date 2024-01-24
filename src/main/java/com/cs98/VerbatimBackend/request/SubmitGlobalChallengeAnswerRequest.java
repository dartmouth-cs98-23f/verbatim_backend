package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class SubmitGlobalChallengeAnswerRequest {
    private String username;
    private String responseQ1;
    private String responseQ2;
    private String responseQ3;
    private String responseQ4;
    private String responseQ5;
}
