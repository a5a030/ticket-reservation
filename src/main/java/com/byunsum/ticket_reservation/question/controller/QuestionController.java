package com.byunsum.ticket_reservation.question.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.question.dto.QuestionRequestDto;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "문의글 API", description = "1:1 문의글 등록 및 조회 API")
@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "문의글 등록", description = "로그인한 사용자가 1:1 문의글을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의글 등록 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
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

    @Operation(summary = "내 문의글 목록 조회", description = "로그인한 사용자가 등록한 본인의 문의글을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<QuestionResponseDto>> getMyQuestions(@AuthenticationPrincipal Member member) {
        List<QuestionResponseDto> questions = questionService.findMyQuestions(member);

        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "전체 문의글 조회 (관리자)", description = "관리자가 등록된 모든 문의글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (관리자 아님)")
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        List<QuestionResponseDto> questions = questionService.findAllQuestions();
        return ResponseEntity.ok(questions);
    }
}
