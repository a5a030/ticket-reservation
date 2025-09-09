package com.byunsum.ticket_reservation.payment.repository;

import com.byunsum.ticket_reservation.payment.domain.RefundHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundHistoryRepository extends JpaRepository<RefundHistory, Long> {
}
