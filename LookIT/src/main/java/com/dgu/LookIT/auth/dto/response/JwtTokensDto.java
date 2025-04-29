package com.dgu.LookIT.auth.dto.response;

import lombok.Builder;

@Builder
public record JwtTokensDto (
        String accessToken,
        String refreshToken
){
}
