package com.dnd.snappy.domain.member.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class Member extends BaseEntity {

    public static Member Id(Long memberId) {
        return Member.builder()
                .id(memberId)
                .build();
    }

    public static Member create() {
        return new Member();
    }
}
