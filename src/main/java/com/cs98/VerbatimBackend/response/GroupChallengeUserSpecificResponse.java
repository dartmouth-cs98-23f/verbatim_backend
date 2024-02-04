package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupChallengeUserSpecificResponse {

    @JsonManagedReference
    private GroupChallenge groupChallenge;

    List<Object> questions;

    Boolean userHasCompleted;

    private List<GroupAnswers> groupAnswers;

    private int totalResponses;

    private List<User> verbaMatch;

    private double verbaMatchSimilarity;


}
