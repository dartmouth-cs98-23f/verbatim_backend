package com.cs98.VerbatimBackend.response;

import com.cs98.VerbatimBackend.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupStats {
    double groupRating;
    List<User> verbaMatch;
    List<String> groupMembers;
    int groupId;

}
