package com.azathorpe.ui;

import java.util.List;

public record ProblemDto(
        int contestId,
        String index,
        String problemsetName,
        String name,
        String type,
        float points,
        int rating,
        List<String> tags,
        String contestName,
        String division
) {}