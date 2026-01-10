package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.question.domain.Answer;
import com.byunsum.ticket_reservation.question.domain.Question;
import com.byunsum.ticket_reservation.question.dto.AnswerRequestDto;
import com.byunsum.ticket_reservation.question.repository.AnswerRepository;
import com.byunsum.ticket_reservation.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public AdminAnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public void createAnswer(Long questionId, AnswerRequestDto dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 질문입니다."));

        if(question.getAnswer() != null){
            throw new RuntimeException("이미 답변이 등록된 문의글입니다.");
        }
        Answer answer = new Answer(dto.getContent(),  question);
        answerRepository.save(answer);
    }
}
