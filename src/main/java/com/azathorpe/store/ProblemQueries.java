package com.azathorpe.store;

import com.azathorpe.entities.Problem;
import com.azathorpe.ui.ProblemDto;
import com.azathorpe.ui.ProblemFilter;
import com.azathorpe.utils.dbUtils.ProblemStore;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.ArrayList;
import java.util.List;

public final class ProblemQueries {

    private ProblemQueries() {}

    /** 事务内把 Entity 映射为 DTO，Entity 不能带出事务 */
    public static ProblemDto toDto(Entity e) {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) e.getProperty("tags"); // 如果 tags 尚未入库，返回空列表
        return new ProblemDto(
                (Integer) e.getProperty("contestId"),
                (String) e.getProperty("index"),
                (String) e.getProperty("problemsetName"),
                (String) e.getProperty("name"),
                (String) e.getProperty("type"),
                e.getProperty("points") == null ? 0f : ((Number) e.getProperty("points")).floatValue(),
                e.getProperty("rating") == null ? 0 : (Integer) e.getProperty("rating"),
                tags == null ? List.of() : tags
        );
    }

    public static List<ProblemDto> loadAll() {
        List<ProblemDto> result = new ArrayList<>();
        ProblemStore.get().executeInReadonlyTransaction(txn -> {
            for (Entity e : txn.getAll("Problem")) {
                result.add(toDto(e));
            }
        });
        return result;
    }

    public static List<Integer> loadContestIds() {
        List<Integer> result = new ArrayList<>();
        ProblemStore.get().executeInReadonlyTransaction(txn -> {
            for (Entity e : txn.getAll("Contest")) {
                Integer id = (Integer) e.getProperty("contestId");
                if (id != null) result.add(id);
            }
        });
        result.sort(Integer::compareTo);
        return result;
    }

    public static List<ProblemDto> loadByContest(int contestId) {
        List<ProblemDto> result = new ArrayList<>();
        ProblemStore.get().executeInReadonlyTransaction(txn -> {
            for (Entity e : txn.find("Problem", "contestId", contestId)) {
                result.add(toDto(e));
            }
        });
        result.sort((a, b) -> a.index().compareTo(b.index()));
        return result;
    }

    public static List<ProblemDto> query(ProblemFilter filter) {
        List<ProblemDto> result = new ArrayList<>();
        ProblemStore.get().executeInReadonlyTransaction(txn -> {
            EntityIterable base;
            if (filter.contestId() != null) {
                base = txn.find("Problem", "contestId", filter.contestId());
            } else {
                base = txn.getAll("Problem");
            }
            for (Entity e : base) {
                ProblemDto dto = toDto(e);
                if (matches(dto, filter)) {
                    result.add(dto);
                }
            }
        });
        result.sort((a, b) -> {
            int c = Integer.compare(a.contestId(), b.contestId());
            return c != 0 ? c : a.index().compareTo(b.index());
        });
        return result;
    }

    private static boolean matches(ProblemDto p, ProblemFilter f) {
        if (f.nameKeyword() != null && !f.nameKeyword().isBlank()
                && !p.name().toLowerCase().contains(f.nameKeyword().toLowerCase())) {
            return false;
        }
        if (f.ratingMin() != null && p.rating() < f.ratingMin()) return false;
        if (f.ratingMax() != null && p.rating() > f.ratingMax()) return false;
        if (f.type() != null && !"全部".equals(f.type()) && !f.type().equals(p.type())) return false;
        if (f.tags() != null && !f.tags().isEmpty()) {
            for (String t : f.tags()) {
                if (p.tags().stream().noneMatch(x -> x.equalsIgnoreCase(t))) return false;
            }
        }
        return true;
    }
}