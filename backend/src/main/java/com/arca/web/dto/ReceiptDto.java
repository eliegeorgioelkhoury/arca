package com.arca.web.dto;

import com.arca.domain.Receipt;
import java.time.Instant;

public record ReceiptDto(
        Long id,
        String originalFilename,
        String contentType,
        Long sizeBytes,
        Instant uploadedAt) {

    public static ReceiptDto from(Receipt r) {
        return new ReceiptDto(
                r.getId(),
                r.getOriginalFilename(),
                r.getContentType(),
                r.getSizeBytes(),
                r.getUploadedAt());
    }
}
