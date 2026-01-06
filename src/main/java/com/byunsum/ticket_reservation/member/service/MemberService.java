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
        validateMember(member);

        if(member.getRole() == null) {
            member.setRole("ROLE_USER");
        }

        memberRepository.save(member);

        return member.getId();
    }

    private void validateMember(Member member) {
        if(member.getLoginId() == null || member.getLoginId().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> {
                    throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
                });
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findById(Long memberId) {
        return  memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

//    public Optional<Member> findOne(Long memberId) {
//        return memberRepository.findById(memberId);
//    }


    @Transactional
    public void update(Long memberId, String username) {
        Member member = findById(memberId);
        member.setUsername(username);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " +  loginId));
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