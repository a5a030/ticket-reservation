package com.byunsum.ticket_reservation.member.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    public MemberService(@Qualifier("jpaMemberRepository") MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public long join(Member member) {
        if(member.getRole() == null) {
            member.setRole("ROLE_USER");
        }

        validateMember(member);

        memberRepository.save(member);

        return member.getId();
    }

    private void validateMember(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
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

//    public Member login(String loginId, String rawPassword) {
//        Optional<Member> findMember = memberRepository.findByLoginId(loginId);
//
//        if (findMember.isEmpty()) {
//            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
//        }
//
//        Member member = findMember.get();
//        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
//            throw new CustomException(ErrorCode.INVALID_PASSWORD);
//        }
//
//        return member;
//    }

    @Transactional
    public void update(Long memberId, String username) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setUsername(username);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

//    public Member authenticate(String loginId, String password) {
//        Member member = memberRepository.findByLoginId(loginId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
//
//        if (!passwordEncoder.matches(password, member.getPassword())) {
//            throw new CustomException(ErrorCode.INVALID_PASSWORD);
//        }
//
//        return member;
//    }
//
//    public void register(String loginId, String username, String rawPassword, String email) {
//        Optional<Member> existing = memberRepository.findByLoginId(loginId);
//
//        if (existing.isPresent()) {
//            throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
//        }
//
//        Member member = new Member();
//        member.setLoginId(loginId);
//        member.setUsername(username);
//        member.setPassword(passwordEncoder.encode(rawPassword));
//        member.setEmail(email);
//        member.setRole("ROLE_USER");
//
//        memberRepository.save(member);
//    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }


    @Transactional
    public void updateRefreshToken(String loginId, String refreshToken) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setRefreshToken(refreshToken);
    }

    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}