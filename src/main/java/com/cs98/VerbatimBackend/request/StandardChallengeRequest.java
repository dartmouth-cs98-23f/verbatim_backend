package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class StandardChallengeRequest {
    private String groupName;

    private String createdByUsername;
}
