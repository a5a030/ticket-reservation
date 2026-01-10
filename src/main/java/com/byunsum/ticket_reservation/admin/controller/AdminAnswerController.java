package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.question.dto.AnswerRequestDto;
import com.byunsum.ticket_reservation.admin.service.AdminAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "문의 답변 API", description = "관리자 전용 답변 등록 API")
@RestController
@RequestMapping("/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnswerController {
    private final AdminAnswerService adminAnswerService;

    public AdminAnswerController(AdminAnswerService adminAnswerService) {
        this.adminAnswerService = adminAnswerService;
    }

    @Operation(summary = "답변 등록", description = "관리자가 특정 문의글에 대해 답변을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "답변 등록 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 문의글 없음")
    })
    @PostMapping("/{questionId}/answer")
    public ResponseEntity<String> createAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerRequestDto dto) {
        adminAnswerService.createAnswer(questionId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("답변이 등록됐습니다.");
    }
}
