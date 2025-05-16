package com.dgu.LookIT.fitting.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "style_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StyleAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_Analysis")
    private BodyAnalysis bodyAnalysis;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_type")
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "face_shape")
    private FaceShape faceShape;

    @Column(name = "analysis_date")
    private LocalDateTime analysisDate;

    // 생성자 추가: userId만 받아서 새로운 엔티티를 만들 때 사용
    public StyleAnalysis(Long userId) {
        this.userId = userId;
        this.analysisDate = LocalDateTime.now(); // 생성 시점
    }
}
