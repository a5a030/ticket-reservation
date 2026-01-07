package com.byunsum.ticket_reservation.legacy.member;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.legacy.member.form.MemberForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    public String create(@RequestParam String loginId, @RequestParam String password) {
        Member member = new Member();
        member.setLoginId(loginId);
        member.setPassword(passwordEncoder.encode(password));

        try {
            memberService.join(member);

            return "redirect:/web/members/join-form?success";
        } catch (IllegalStateException e) {
            return "redirect:/web/members/join-form?error";
        }
    }

    @GetMapping("/edit")
    public String editForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        if(loginMember==null){
            return "redirect:/web/login-form?error=unauthorized";
        }

        model.addAttribute("member", loginMember);
        return "edit";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam String name, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return "redirect:/web/login-form?error=unauthorized";
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        if(loginMember==null){
            return "redirect:/web/login-form?error=unauthorized";
        }

        memberService.update(loginMember.getId(), name);
        loginMember.setName(name);

        return  "redirect:/web/members/me?updated";
    }

    @PostMapping("/withdraw")
    public String withdraw(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginMember") == null){
            return "redirect:/web/login-form?error=unauthorized";
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        memberService.deleteById(loginMember.getId());
        session.invalidate();

        return "redirect:/web/login-form?withdrawSuccess";
    }
}
