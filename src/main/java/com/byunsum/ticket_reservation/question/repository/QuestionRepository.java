package com.byunsum.ticket_reservation.question.repository;

import com.byunsum.ticket_reservation.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByAuthorId(Long authorId);
    Page<Question> findByAuthorId(Long authorId, Pageable pageable);
}
