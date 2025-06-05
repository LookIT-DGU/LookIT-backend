package com.dgu.LookIT.brand.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "brand_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"brand_id", "tag_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonBackReference
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @JsonBackReference
    private Tag tag;

    // 필요 시 추가 필드 예시
    // @Column(name = "priority")
    // private Integer priority;
}
