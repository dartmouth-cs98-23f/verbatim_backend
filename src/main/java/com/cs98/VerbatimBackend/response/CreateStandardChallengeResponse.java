package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.GroupChallenge;
import com.cs98.VerbatimBackend.model.Question;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateStandardChallengeResponse {

    private GroupChallenge groupChallenge;

    private List<Question> questionList;
}
