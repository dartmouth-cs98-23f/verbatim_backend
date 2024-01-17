package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupChallengeUserSpecificResponse {
    private GroupChallenge groupChallenge;

    private List<GroupAnswers> groupAnswers;

    private int totalResponses;

    private List<User> verbaMatch;

    private double verbaMatchSimilarity;


}
