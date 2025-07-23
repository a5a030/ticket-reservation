package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.dto.SessionMemberDTO;
import com.byunsum.ticket_reservation.member.form.MemberForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

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

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody MemberForm form) {
        try {
            Member member = new Member();
            member.setName(form.getName());
            member.setPassword(form.getPassword());
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

    @GetMapping
    public List<Member> list() {
        return memberService.findMembers(); //@RestController에서 json 배열로 반환
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginMember") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태가 아닙니다.");
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        return ResponseEntity.ok(new SessionMemberDTO(loginMember.getId(), loginMember.getName()));
    }
}
