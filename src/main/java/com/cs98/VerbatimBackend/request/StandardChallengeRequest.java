package com.cs98.VerbatimBackend.request;

import lombok.Data;

@Data
public class StandardChallengeRequest {
    private int groupId;

    private String createdByUsername;
}
