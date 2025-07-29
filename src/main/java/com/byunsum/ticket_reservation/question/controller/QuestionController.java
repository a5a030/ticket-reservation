package com.byunsum.ticket_reservation.question.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.question.dto.QuestionRequestDto;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(
            @RequestBody QuestionRequestDto dto,  @AuthenticationPrincipal Member member
    ) {
        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        QuestionResponseDto response = questionService.create(dto, member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuestionResponseDto>> getMyQuestions(@AuthenticationPrincipal Member member) {
        List<QuestionResponseDto> questions = questionService.findMyQuestions(member);

        return ResponseEntity.ok(questions);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        List<QuestionResponseDto> questions = questionService.findAllQuestions();
        return ResponseEntity.ok(questions);
    }
}
