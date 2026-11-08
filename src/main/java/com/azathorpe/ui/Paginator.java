package com.azathorpe.ui;

import java.util.ArrayList;
import java.util.List;

/** 简单的分页器：按“每页 N 个 contest”切分 */
public class Paginator<T> {

    private final List<T> all;
    private final int pageSize;
    private int currentPage = 0;

    public Paginator(List<T> all, int pageSize) {
        this.all = all;
        this.pageSize = Math.max(1, pageSize);
    }

    public int pageCount() {
        return Math.max(1, (all.size() + pageSize - 1) / pageSize);
    }

    public int currentPage() {
        return currentPage;
    }

    public void goTo(int page) {
        currentPage = Math.max(0, Math.min(page, pageCount() - 1));
    }

    public boolean hasPrev() { return currentPage > 0; }
    public boolean hasNext() { return currentPage < pageCount() - 1; }

    public List<T> currentItems() {
        int from = currentPage * pageSize;
        int to = Math.min(from + pageSize, all.size());
        if (from >= all.size()) return List.of();
        return new ArrayList<>(all.subList(from, to));
    }

    public List<T> all() { return all; }
}