package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetGlobalChallengeQuestionResponse {

    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String q5;

    private String categoryQ1;
    private String categoryQ2;
    private String categoryQ3;
    private String categoryQ4;
    private String categoryQ5;

}
