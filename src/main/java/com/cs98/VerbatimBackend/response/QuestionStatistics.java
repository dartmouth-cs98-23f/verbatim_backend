package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.GlobalChallengeUserResponse;
import com.cs98.VerbatimBackend.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QuestionStatistics {

    private String firstMostPopular;
    private int numResponsesFirst;

    private String secondMostPopular;
    private int numResponsesSecond;

    private String thirdMostPopular;
    private int numResponsesThird;

    private Map<String, String> friendResponses;

}
