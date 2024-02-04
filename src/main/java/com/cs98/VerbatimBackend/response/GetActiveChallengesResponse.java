package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetActiveChallengesResponse {
    List<GroupChallenge> activeChallenges;
    int groupId;
}
