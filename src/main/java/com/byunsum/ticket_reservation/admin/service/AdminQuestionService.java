package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminQuestionService {
    private final QuestionRepository questionRepository;

    public AdminQuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> findAllQuestions(Pageable pageable) {
        return  questionRepository.findAll(pageable)
                .map(QuestionResponseDto::new);
    }
}
