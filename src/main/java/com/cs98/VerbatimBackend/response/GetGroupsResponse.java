package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.UserGroup;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetGroupsResponse {
    private List<UserGroup> groups;
}
