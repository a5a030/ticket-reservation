package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
public class MyPageController {
    @GetMapping
    public ResponseEntity<String> getMyPage(@AuthenticationPrincipal Member member) {
        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String name =  member.getName();
        String email = member.getEmail();
        String role = member.getRole();

        String response = String.format("마이페이지 - %s / %s / %s", name, email, role);
        return ResponseEntity.ok(response);
    }
}
