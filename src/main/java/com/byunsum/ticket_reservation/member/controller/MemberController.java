package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.dto.SessionMemberDTO;
import com.byunsum.ticket_reservation.member.form.MemberForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 API", description = "회원 가입 및 정보 조회 관련 API")
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Operation(summary = "회원 가입", description = "회원 정보를 받아 새 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자 이름")
    })
    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody MemberForm form) {
        try {
            Member member = new Member();
            member.setLoginId(form.getLoginId());
            String encodedPassword = passwordEncoder.encode(form.getPassword());
            member.setPassword(encodedPassword); //비밀번호 암호화

            System.out.println("암호화된 비밀번호: "+encodedPassword);

            memberService.join(member);
            return ResponseEntity.ok(member); // 200 ok + member json
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /*
    @GetMapping
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList"; //HTML 뷰 렌더링
    }
    */

    @Operation(summary = "전체 회원 조회", description = "등록된 모든 회원 목록을 반환합니다. (관리자 전용)")
    @GetMapping
    public List<Member> list() {
        return memberService.findMembers(); //@RestController에서 json 배열로 반환
    }

    @Operation(summary = "로그인한 회원 정보 조회", description = "세션을 기반으로 로그인한 사용자의 ID와 이름을 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<?> getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        /* 인터셉터에서 검증하므로 세션 null 체크할 필요 없어짐
        if(session == null || session.getAttribute("loginMember") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태가 아닙니다.");
        }
         */

        Member loginMember = (Member) session.getAttribute("loginMember");
        return ResponseEntity.ok(new SessionMemberDTO(loginMember.getId(), loginMember.getLoginId()));
    }

    @Operation(summary = "보호된 페이지 접근", description = "로그인한 사용자만 접근 가능한 페이지를 테스트합니다.")
    @GetMapping("/secret")
    public ResponseEntity<?> secretPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Member loginMember = (session != null) ? (Member) session.getAttribute("loginMember") : null;

        if(loginMember == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        return ResponseEntity.ok("로그인한 이용자만 접근 가능한 페이지입니다.");
    }
}
