package com.azathorpe.ui;

import java.util.*;

public final class ProblemMatrixBuilder {

    private ProblemMatrixBuilder() {
    }

    /**
     * 传入当前页的题目，按 contestId 分组，列 = baseIndex
     */
    public static MatrixData build(List<ProblemDto> problems) {
        TreeSet<String> baseIndexes = new TreeSet<>();
        Map<Integer, Map<String, List<ProblemDto>>> rows = new TreeMap<>(Comparator.reverseOrder());

        for (ProblemDto p : problems) {
            String base = baseIndex(p.index());
            baseIndexes.add(base);
            rows.computeIfAbsent(p.contestId(), k -> new TreeMap<>())
                    .computeIfAbsent(base, k -> new ArrayList<>())
                    .add(p);
        }
        for (Map<String, List<ProblemDto>> row : rows.values()) {
            for (List<ProblemDto> cell : row.values()) {
                cell.sort(Comparator.comparing(ProblemDto::index));
            }
        }
        return new MatrixData(new ArrayList<>(baseIndexes), rows);
    }

    /**
     * C1 -> C，C12 -> C，A -> A
     */
    public static String baseIndex(String index) {
        if (index == null || index.isEmpty()) return "";
        int i = index.length();
        while (i > 0 && Character.isDigit(index.charAt(i - 1))) {
            i--;
        }
        return index.substring(0, i);
    }

    public record MatrixData(List<String> baseIndexes,
                             Map<Integer, Map<String, List<ProblemDto>>> rows) {
    }
}