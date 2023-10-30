package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionStatistics {

    private String firstMostPopular;
    private int numResponsesFirst;

    private String secondMostPopular;
    private int numResponsesSecond;

    private String thirdMostPopular;
    private int numResponsesThird;

}
