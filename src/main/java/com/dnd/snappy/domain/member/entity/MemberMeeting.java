package com.dnd.snappy.domain.member.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class MemberMeeting extends BaseEntity {

    public static final int MAX_SHOOT_COUNT = 10;

    @Column(nullable = false, length = 8)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Integer shootCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;



    public static MemberMeeting create(String nickname, Member member, Meeting meeting, Role role) {
        return MemberMeeting.builder()
                .nickname(nickname)
                .role(role)
                .shootCount(MAX_SHOOT_COUNT)
                .meeting(meeting)
                .member(member)
                .build();
    }
}
