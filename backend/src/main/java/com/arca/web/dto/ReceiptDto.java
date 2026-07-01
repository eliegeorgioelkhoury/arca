package com.arca.web.dto;

import com.arca.domain.Receipt;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReceiptDto(
        Long id,
        String originalFilename,
        String contentType,
        Long sizeBytes,
        Instant uploadedAt,
        String url) {

    public static ReceiptDto from(Receipt r, String url) {
        return new ReceiptDto(
                r.getId(),
                r.getOriginalFilename(),
                r.getContentType(),
                r.getSizeBytes(),
                r.getUploadedAt(),
                url);
    }
}
