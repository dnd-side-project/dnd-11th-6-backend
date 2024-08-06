package com.dnd.snappy.domain.member.service;

import static com.dnd.snappy.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.member.entity.Member;
import com.dnd.snappy.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND, memberId))
                .getId();
    }

    public Long createMember() {
        Member newMember = memberRepository.save(Member.create());
        return newMember.getId();
    }

}
