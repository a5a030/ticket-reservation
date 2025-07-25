package com.byunsum.ticket_reservation.question.repository;

import com.byunsum.ticket_reservation.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
