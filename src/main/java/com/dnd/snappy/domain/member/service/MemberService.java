package com.dnd.snappy.domain.member.service;

import com.dnd.snappy.domain.member.entity.Member;
import com.dnd.snappy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long createMember() {
        Member member = memberRepository.save(Member.create());
        return member.getId();
    }

}
