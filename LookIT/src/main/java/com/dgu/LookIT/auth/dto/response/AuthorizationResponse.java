package com.dgu.LookIT.auth.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthorizationResponse(
        @JsonProperty("is_member")
        Boolean isMember,

        @JsonProperty("jwt")
        JwtTokensDto jwtTokensDto
) {
    public static AuthorizationResponse of(Boolean isMember, JwtTokensDto jwtTokensDto) {
        return new AuthorizationResponse(isMember, jwtTokensDto);
    }
}

