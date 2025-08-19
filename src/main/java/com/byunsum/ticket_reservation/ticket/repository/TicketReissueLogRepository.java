package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketReissueLog;
import org.springframework.data.repository.CrudRepository;

public interface TicketReissueLogRepository extends CrudRepository<TicketReissueLog, Long> {

}
