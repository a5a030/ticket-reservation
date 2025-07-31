package com.byunsum.ticket_reservation.member.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaMemberRepository implements MemberRepository {
    private final SpringDataJpaMemberRepository jpaRepository;

    public JpaMemberRepository(SpringDataJpaMemberRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Member save(Member member) {
        return jpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return jpaRepository.findByLoginId(loginId);
    }

    @Override
    public List<Member> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
