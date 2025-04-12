package com.example.userservice.common.response;

import java.util.List;

public record CursorPageResponse<T>(
    List<T> list,
    String nextCursor, // 다음 페이지를 위한 커서
    boolean hasNext    // 다음 페이지 존재 여부
) {
    public static <T> CursorPageResponse<T> of(List<T> list, String nextCursor, boolean hasNext) {
        return new CursorPageResponse<>(list, nextCursor, hasNext);
    }
}
