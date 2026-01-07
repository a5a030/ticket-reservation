package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.auth.dto.SignupRequestDto;
import com.byunsum.ticket_reservation.auth.dto.SignupResponseDto;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.dto.MyPageResponseDto;
import com.byunsum.ticket_reservation.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 API", description = "회원 가입 및 정보 조회 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }


    /* @Controller용
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }
    */

    /* @Controller용
    @PostMapping("/new")
    public String create(@ModelAttribute MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());
        member.setPassword(form.getPassword()); //추후 암호화 적용
        memberService.join(member);

        return "redirect:/";
    }
    */

    @Operation(summary = "회원 가입", description = "새 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자 이름")
    })
    @PostMapping
    public ResponseEntity<SignupResponseDto> create(@RequestBody SignupRequestDto dto) {
        Member member = new Member();
        member.setLoginId(dto.getLoginId());
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        member.setPassword(encodedPassword); //비밀번호 암호화

        memberService.join(member);

        SignupResponseDto responseDto = new SignupResponseDto(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getEmail()
        );


        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "전체 회원 조회(관리자)", description = "등록된 모든 회원 목록을 반환합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Member> list() {
        return memberService.findMembers(); //@RestController에서 json 배열로 반환
    }

    @Operation(summary = "내 정보 조회", description = "JWT 인증된 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDto> getLoginMember(@AuthenticationPrincipal Member member) {
        MyPageResponseDto responseDto = new MyPageResponseDto(
                member.getLoginId(),
                member.getName(),
                member.getEmail(),
                member.getRole()
        );

        return  ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "보호된 페이지 접근", description = "로그인한 사용자만 접근 가능한 페이지를 테스트합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/secret")
    public ResponseEntity<?> secretPage() {
        return ResponseEntity.ok("로그인한 이용자만 접근 가능한 페이지입니다.");
    }
}
