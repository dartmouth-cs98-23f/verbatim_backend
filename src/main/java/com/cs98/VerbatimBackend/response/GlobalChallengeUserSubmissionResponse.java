package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalChallengeUserSubmissionResponse {

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

//    private String mostPopularQ1;
//    private String mostPopularQ2;
//    private String mostPopularQ3;

//
//    private int numMostPopularQ1;
//
//    private int numMostPopularQ2;
//
//    private int numMostPopularQ3;

}
