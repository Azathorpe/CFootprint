package com.azathorpe.ui;

import java.util.List;

public record ProblemFilter(
        Integer contestId,      // null 表示不按 contest 筛
        String nameKeyword,     // null 或空表示不筛
        Integer ratingMin,
        Integer ratingMax,
        String type,            // null 或 "全部" 表示不筛
        List<String> tags       // 空表示不筛
) {
    public boolean isEmpty() {
        return contestId == null
                && (nameKeyword == null || nameKeyword.isBlank())
                && ratingMin == null && ratingMax == null
                && (type == null || "全部".equals(type))
                && (tags == null || tags.isEmpty());
    }
}