package com.byunsum.ticket_reservation.question.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.question.dto.QuestionRequestDto;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private MemberRepository memberRepository;

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(
            @RequestBody QuestionRequestDto dto
//            , @RequestAttribute("loginMember") Member loginMemer //jwt 인증 사용자 주입 방식
    ) {
        // 테스트용 멤버 생성
        Member member = new Member();
        member.setName("tester");
        member = memberRepository.save(member);

        QuestionResponseDto response = questionService.create(dto, member);
        return ResponseEntity.ok(response);
    }
}
