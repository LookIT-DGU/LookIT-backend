package com.dgu.LookIT.fitting.domain;

import com.dgu.LookIT.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "style_recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StyleRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와의 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Column(name = "styling_tips", columnDefinition = "TEXT")
    private String stylingTips;

    @Column(name = "recommendation_date")
    private LocalDateTime recommendationDate;

    @PrePersist
    protected void onCreate() {
        this.recommendationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.recommendationDate = LocalDateTime.now();
    }
}
