package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class GetUserStatsResponse {
    Integer streak;
    Integer groupChalllengesCompleted;
    Integer globalChallengesCompleted;
    Integer numFriends;
    User verbaMatchUser;
    Double verbaMatchScore;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    Date friendsSince;
}
