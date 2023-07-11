package com.mysql.jwt.db.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    // pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    // 사용자 타입(NORMAL : 일반 회원, KAKAO : 카카오, GOOGLE : 구글, TWITTER : 트위터)
    @Column(nullable = false, length = 15)
    private String type;

    // 이메일 - 한 가지 이메일로 여러 소셜 계정에 로그인할 수 있으므로 unique 설정은 안 함.
    @Column(nullable = false, length = 100)
    private String email;

    // 비밀번호
    @Column(length = 100)
    private String password;

    // refresh token
    @Column(name = "refresh_token", unique = true, length = 200)
    private String refreshToken;

    // 가입일시
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    // 수정일시
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // 회원상태(A : 활성화, D : 탈퇴)
    @Column(nullable = false)
    private char status;

    // Default Value 설정
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.status = 'A';
    }

    // 일반 회원 회원가입
    @Builder
    public User(String type, String email) {
        this.type = type;
        this.email = email;
    }

    // refresh token 저장
    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // refresh token DB에서 삭제
    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

}
