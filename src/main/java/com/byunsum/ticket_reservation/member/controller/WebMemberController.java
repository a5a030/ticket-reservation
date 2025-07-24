package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.form.MemberForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/members")
public class WebMemberController {
    private MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebMemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/join-form")
    public String joinForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "join";
    }

    @PostMapping("/new")
    public String create(@RequestParam String name, @RequestParam String password) {
        Member member = new Member();
        member.setName(name);
        member.setPassword(passwordEncoder.encode(password));

        try {
            memberService.join(member);

            return "redirect:/web/members/join-form?success";
        } catch (IllegalStateException e) {
            return "redirect:/web/members/join-form?error";
        }
    }
}
