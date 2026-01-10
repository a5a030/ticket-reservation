package com.byunsum.ticket_reservation.question.service;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.question.domain.Question;
import com.byunsum.ticket_reservation.question.dto.QuestionRequestDto;
import com.byunsum.ticket_reservation.question.dto.QuestionResponseDto;
import com.byunsum.ticket_reservation.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional
    public QuestionResponseDto create(QuestionRequestDto dto, Member member) {
        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setAuthor(member);

        Question saved = questionRepository.save(question);

        return new QuestionResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDto> findMyQuestions(Long authorId) {
        List<Question> questions = questionRepository.findByAuthorId(authorId);

        return questions.stream()
                .map(q -> new QuestionResponseDto(q))
                .collect(Collectors.toList());
    }
}

