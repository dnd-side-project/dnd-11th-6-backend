package com.dnd.snappy.domain.member.repository;

import com.dnd.snappy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
