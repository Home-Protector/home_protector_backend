package com.sparta.home_protector.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 다른 entity 클래스에서 상속받으면 이 추상 클래스에 선언한 멤버를 컬럼으로 인식함
@EntityListeners(AuditingEntityListener.class) // 이 클래스에 Auditing 기능 포함 시켜줌. / Auditing : 시간 자동 추가 기능
public abstract class Timestamped {

    @CreatedDate // 객체 생성 후 저장될 때 시간을 자동으로 저장
    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입 매핑해줌 (TIMESTAMP : 날짜 + 시간 +초단위)
    @Column(updatable = false) // updatable 옵션 : 값이 수정될 때 시간 저장을 하지 않도록 방지
    private LocalDateTime createdAt; // 생성시간 필드

    @LastModifiedDate // 객체의 값이 변경될 때 변경되는 시간을 자동 저장
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt; // 마지막 수정 시간 필드
}
