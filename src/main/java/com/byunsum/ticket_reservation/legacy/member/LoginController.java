package com.byunsum.ticket_reservation.legacy.member;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.legacy.member.form.LoginForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 API", description = "로그인/로그아웃 관련 API")
@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "세션 로그인", description = "loginId(아이디)와 password로 세션 로그인합니다. 로그인 성공 시 세션과 쿠키가 생성됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패: 잘못된 아이디 혹은 비밀번호")
    })
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginForm loginForm,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 기존세션 무효화
            HttpSession oldSession = request.getSession();
            if(oldSession != null) {
                oldSession.invalidate();
            }

            // 로그인 검증
            Member loginMember = memberService.login(loginForm.getLoginId(), loginForm.getPassword());

            // 새 세션 생성
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("loginMember", loginMember);

            Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
            cookie.setMaxAge(60 * 60 * 24 * 7); //7일
            cookie.setPath("/");
            response.addCookie(cookie);

//            newSession.setMaxInactiveInterval(30 * 60);

//            return ResponseEntity.ok("로그인 성공");
            return ResponseEntity.ok("/web/members/me"); //프론트에서 리디렉션 처리

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "로그아웃 성공";
    }
}
