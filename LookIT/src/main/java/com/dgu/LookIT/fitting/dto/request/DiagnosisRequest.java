package com.dgu.LookIT.fitting.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DiagnosisRequest(
        @JsonProperty("bodyType")
        String bodyType
) {
}
