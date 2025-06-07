package com.dgu.LookIT.brand.dto.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandStyleResponse {
    private String brandName;
    private String brandUrl;
    private String styleName;
}
