package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GlobalChallengeUserSpecificResponse {
    private String q1;
    private String q2;
    private String q3;

    private String categoryQ1;
    private String categoryQ2;
    private String categoryQ3;

    private String responseQ1;
    private String responseQ2;
    private String responseQ3;

    private int totalResponses;

    private int numVerbatimQ1;

    private int numVerbatimQ2;

    private int numVerbatimQ3;

    private int numExactVerbatim;

    private QuestionStatistics statsQ1;

    private QuestionStatistics statsQ2;

    private QuestionStatistics statsQ3;

    private Map<String, List<String>> verbatasticUsers;


}
