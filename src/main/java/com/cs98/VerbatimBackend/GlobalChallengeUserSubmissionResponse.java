package com.cs98.VerbatimBackend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalChallengeUserSubmissionResponse {

    private String responseQ1;
    private String responseQ2;
    private String responseQ3;

}
