package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupStats {
    double groupRating;
    List<String> groupMembers;

}
