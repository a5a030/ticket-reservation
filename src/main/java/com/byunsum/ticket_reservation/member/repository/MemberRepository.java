package com.byunsum.ticket_reservation.member.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByLoginId(String loginId);
    List<Member> findAll();
    void deleteById(Long id);
}
