package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserGroupCreationResponse {

    private Integer groupId;
    private String groupName;
    private List<User> users;
}
