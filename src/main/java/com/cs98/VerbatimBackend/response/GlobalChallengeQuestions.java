package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GlobalChallengeQuestions {

    private String q1;
    private String q2;
    private String q3;

    private String categoryQ1;
    private String categoryQ2;
    private String categoryQ3;

    private int globalChallengeId;
}
