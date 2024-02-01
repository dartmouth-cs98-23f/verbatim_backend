package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetGroupChallengeQuestionsResponse {
    List<Object> questions;

    Boolean userHasCompleted;
}
