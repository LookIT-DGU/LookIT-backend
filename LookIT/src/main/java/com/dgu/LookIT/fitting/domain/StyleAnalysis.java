package com.dgu.LookIT.fitting.domain;


import com.dgu.LookIT.user.domain.User;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

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
        this.user = User.builder().id(userId).build(); // ID만 가진 더미 객체
        this.analysisDate = LocalDateTime.now();
    }

}
