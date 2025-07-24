package com.byunsum.ticket_reservation.member.service;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public long join(Member member) {
        validateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Member login(String name, String rawPassword) {
        Optional<Member> findMember = memberRepository.findByName(name);

        if (findMember.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member member = findMember.get();
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    @Transactional
    public void update(Long memberId, String newName, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        member.setName(newName);
        member.setPassword(passwordEncoder.encode(newPassword));
    }
}
