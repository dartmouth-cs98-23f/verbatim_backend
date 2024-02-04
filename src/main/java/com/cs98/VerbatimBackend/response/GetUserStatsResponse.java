package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserStatsResponse {
    Integer streak;
    Integer groupChalllengesCompleted;
    Integer globalChallengesCompleted;
    Integer numFriends;
    User verbaMatchUser;
    Double verbaMatchScore;
}
