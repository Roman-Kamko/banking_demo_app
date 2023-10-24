package com.kamko.bankdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Use for pagination")
public record PageResponse<T>(List<T> content, Metadata metadata) {

    public static <T> PageResponse<T> of(Page<T> page) {
        Metadata metadata = new Metadata(page.getNumber(), page.getSize(), page.getTotalElements());
        return new PageResponse<>(page.getContent(), metadata);
    }

    public record Metadata(int page, int size, long totalElement) {
    }

}
