package com.example.userservice.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> list,
    int totalCount,
    int totalPage,
    int currentPage,
    int pageSize,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PageResponse<T> of(List<T> data, int totalCount, int page, int size) {
        int totalPage = (totalCount - 1) / size + 1;
        boolean hasNext = page < totalPage;
        boolean hasPrevious = page > 1;

        return new PageResponse<>(
            data,
            totalCount,
            totalPage,
            page,
            size,
            hasNext,
            hasPrevious
        );
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber() + 1,
            page.getSize(),
            page.hasNext(),
            page.hasPrevious()
        );
    }

}