package com.cs98.VerbatimBackend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetGlobalChallengeQuestionResponse {

    private String q1;
    private String q2;
    private String q3;

}
