package com.byunsum.ticket_reservation.question.service;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.question.domain.Question;
import com.byunsum.ticket_reservation.question.dto.QuestionRequestDto;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public QuestionResponseDto create(QuestionRequestDto dto, Member author) {
        Question question = new Question(dto.getTitle(), dto.getContent(), author);
        Question saved = questionRepository.save(question);

        return new QuestionResponseDto(saved.getId(), saved.getCreatedAt());
    }
}

