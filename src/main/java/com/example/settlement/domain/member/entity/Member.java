package com.example.settlement.domain.member.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter  //  @Setter 사용 X -> 엔티티의 데이터는 비즈니스 로직에 의해서만 변경되어야 하므로 외부에서 아무나 member.setName()으로
         // 바꾸면 데이터 추적이 어려워져 @Setter를 열어두지 않는다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기술 표준상 엔티티는 기본 생성자가 필수적이다. 외부에서 new Member()로 알멩이 없는 객체
                                                   // 생성을 방지하기 위해 접근제어자를 PROTECTED로 제한하여 안정성 확보
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 55)
    private String name;

    @Enumerated(EnumType.STRING) // 추후에 새로운 Role이 추가되면 Ordinal을 사용하였을 때 기존 데이터가 꼬이는 문제가 발생하여 사전에 String으로 강제하여 방지.
    @Column(nullable = false, length = 20)
    private Role role;   // 일반 회원인지 판매자인지 구분

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
