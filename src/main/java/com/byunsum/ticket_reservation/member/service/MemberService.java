package com.byunsum.ticket_reservation.member.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(@Qualifier("jpaMemberRepository") MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
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
                    throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
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
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Member member = findMember.get();
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return member;
    }

    @Transactional
    public void update(Long memberId, String newName, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setName(newName);
        member.setPassword(passwordEncoder.encode(newPassword));
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public Member authenticate(String name, String password) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return member;
    }

    public void register(String name, String rawPassword, String email) {
        Optional<Member> existing = memberRepository.findByName(name);

        if (existing.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
        }

        Member member = new Member();
        member.setName(name);
        member.setPassword(passwordEncoder.encode(rawPassword));
        member.setEmail(email);
        member.setRole("ROLE_USER");

        memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + name));
    }


    @Transactional
    public void updateRefreshToken(String name, String refreshToken) {
        Member member = memberRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setRefreshToken(refreshToken);
        System.out.println("[DEBUG] 리프레시 토큰 저장: " + refreshToken);
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}