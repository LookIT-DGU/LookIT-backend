package com.dgu.LookIT.fitting.dto.response;

import com.dgu.LookIT.fitting.domain.VirtualFitting;

import java.time.LocalDateTime;

public record FittingResultResponse (
        String resultImageUrl,
        LocalDateTime createdAt
) {
    public static FittingResultResponse from(VirtualFitting entity) {
        return new FittingResultResponse(entity.getResultImageUrl(), entity.getCreatedAt());
    }
}
