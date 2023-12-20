package com.bos.resource.app.common.domain.dto;

public record Paging(
        int page,
        long offset,
        int limit,
        int total
) {
}
