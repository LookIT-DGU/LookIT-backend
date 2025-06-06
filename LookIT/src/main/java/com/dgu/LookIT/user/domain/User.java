package com.dgu.LookIT.user.domain;

import com.dgu.LookIT.auth.dto.request.SignUpRequest;
import com.dgu.LookIT.user.dto.request.MyInformationRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_id")
    private Long serialId;

    @Column(nullable = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(name = "user_image", columnDefinition = "TEXT")
    private String userImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User(Long serialId) {
        this.serialId = serialId;
    }


    public void myInformationUpdate(MyInformationRequest request) {
        this.name = request.name();
        this.gender = request.gender();
        this.birthDate = request.birthday();
        this.userImage = request.image();
    }

    public void signUpUser(SignUpRequest request) {
        this.name = request.name();
        this.gender = request.gender();
        this.birthDate = request.birthday();
        this.userImage = request.image();
    }
}
