package com.byunsum.ticket_reservation.member.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;


public class MemoryMemberRepository implements MemberRepository {
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return store.values().stream()
                .filter(member -> member.getLoginId().equals(loginId))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
