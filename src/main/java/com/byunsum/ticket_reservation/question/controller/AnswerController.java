package com.byunsum.ticket_reservation.question.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.question.domain.Answer;
import com.byunsum.ticket_reservation.question.dto.AnswerRequestDto;
import com.byunsum.ticket_reservation.question.service.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createAnswer(@PathVariable Long questionId, @RequestBody AnswerRequestDto dto, @AuthenticationPrincipal Member admin) {
        answerService.createAnswer(questionId, dto, admin);
        return ResponseEntity.ok("답변이 등록됐습니다.");
    }
}
