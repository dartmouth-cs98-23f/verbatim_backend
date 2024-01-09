package com.cs98.VerbatimBackend.request;

import lombok.Data;

import java.util.List;

@Data
public class CustomChallengeRequest {
    private String groupName;

    private String createdByUsername;

    private List<String> questions;
}
