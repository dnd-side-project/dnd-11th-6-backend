package com.dnd.snappy.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.member.entity.Member;
import com.dnd.snappy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("사용자를 생성한다.")
    @Test
    void createMember() {
        //given
        Member member = Member.builder().id(1L).build();
        given(memberRepository.save(any(Member.class))).willReturn(member);

        //when
        Long memberId = memberService.createMember();

        //then
        assertThat(memberId).isEqualTo(member.getId());
        verify(memberRepository).save(any(Member.class));
    }

}