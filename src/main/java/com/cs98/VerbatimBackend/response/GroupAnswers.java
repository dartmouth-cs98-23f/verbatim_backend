package com.cs98.VerbatimBackend.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GroupAnswers {
    private String question;

    private Map<String, String> responses;
}
