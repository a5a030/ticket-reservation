package com.byunsum.ticket_reservation.config;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {
    @Bean
    public CommandLineRunner initAdmin(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String loginId = "admin";
            String username = "관리자";
            String adminEmail = "admin@example.com";

            if(memberRepository.findByLoginId(loginId).isEmpty()) {
                Member admin = new Member();
                admin.setLoginId(loginId);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ROLE_ADMIN");

                memberRepository.save(admin);

                System.out.println("관리자 계정 자동 생성 완료");
            }
        };
    }
}
