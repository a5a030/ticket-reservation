package com.byunsum.ticket_reservation.member.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
}
