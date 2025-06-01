package com.dgu.LookIT.brand.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDto {
    private String name;
    private String url;
    private List<String> styleTags;
}