package com.dgu.LookIT.fitting.dto.response;

import java.time.LocalDateTime;

public record StyleTipsResponse (
        String stylingTips,
        LocalDateTime recommendationDate
){
}
