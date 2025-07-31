package com.byunsum.ticket_reservation.question.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.question.domain.Answer;
import com.byunsum.ticket_reservation.question.dto.AnswerRequestDto;
import com.byunsum.ticket_reservation.question.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "문의 답변 API", description = "관리자 전용 답변 등록 API")
@RestController
@RequestMapping("/answers")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @Operation(summary = "답변 등록", description = "관리자가 특정 문의글에 대해 답변을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 등록 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 문의글 없음")
    })
    @PostMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createAnswer(@PathVariable Long questionId, @RequestBody AnswerRequestDto dto, @AuthenticationPrincipal Member admin) {
        answerService.createAnswer(questionId, dto, admin);
        return ResponseEntity.ok("답변이 등록됐습니다.");
    }
}
