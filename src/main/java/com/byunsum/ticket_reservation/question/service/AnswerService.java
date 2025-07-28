package com.byunsum.ticket_reservation.question.service;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.question.domain.Answer;
import com.byunsum.ticket_reservation.question.domain.Question;
import com.byunsum.ticket_reservation.question.dto.AnswerRequestDto;
import com.byunsum.ticket_reservation.question.repository.AnswerRepository;
import com.byunsum.ticket_reservation.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnswerService {
    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;

    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public void createAnswer(Long questionId, AnswerRequestDto dto, Member admin) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 질문입니다."));

        Answer answer = new Answer();
        answer.setContent(dto.getContent());
        answer.setQuestion(question);
        answer.setCreatedAt(LocalDateTime.now());

        answerRepository.save(answer);
    }
}
