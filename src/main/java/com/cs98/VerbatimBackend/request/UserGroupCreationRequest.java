package com.cs98.VerbatimBackend.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserGroupCreationRequest {
    private String createdByUsername;
    private String groupName;
    private List<String> usernamesToAdd;
}
