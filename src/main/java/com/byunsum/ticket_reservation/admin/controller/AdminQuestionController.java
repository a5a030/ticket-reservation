package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.admin.service.AdminQuestionService;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "관리자 문의글 API", description = "관리자용 문의글 조회/관리 API")
@RestController
@RequestMapping("/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionController {
    private final AdminQuestionService adminQuestionService;

    public AdminQuestionController(AdminQuestionService adminQuestionService) {
        this.adminQuestionService = adminQuestionService;
    }

    @Operation(summary = "전체 문의글 조회 (관리자)", description = "관리자가 등록된 모든 문의글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자 아님)")
    })
    @GetMapping
    public ResponseEntity<Page<QuestionResponseDto>> getAllQuestions(@ParameterObject Pageable pageable) {
        Page<QuestionResponseDto> result = adminQuestionService.findAllQuestions(pageable);
        return ResponseEntity.ok(result);
    }
}
