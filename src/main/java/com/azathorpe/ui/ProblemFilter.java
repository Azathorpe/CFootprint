package com.azathorpe.ui;

import java.util.List;

public record ProblemFilter(
        Integer contestId,
        String nameKeyword,
        Integer ratingMin,
        Integer ratingMax,
        String type,
        List<String> tags,
        String division       // 新增，"全部" 或 null 表示不筛
) {
    public boolean isEmpty() {
        return contestId == null
                && (nameKeyword == null || nameKeyword.isBlank())
                && ratingMin == null && ratingMax == null
                && (type == null || "全部".equals(type))
                && (tags == null || tags.isEmpty())
                && (division == null || "全部".equals(division));
    }
}