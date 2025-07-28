package com.byunsum.ticket_reservation.question.repository;

import com.byunsum.ticket_reservation.question.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
