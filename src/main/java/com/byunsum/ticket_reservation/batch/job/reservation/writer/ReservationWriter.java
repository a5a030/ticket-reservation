package com.byunsum.ticket_reservation.batch.job.reservation.writer;

import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ReservationWriter implements ItemWriter<Reservation> {
    private final ReservationRepository reservationRepository;

    public ReservationWriter(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void write(Chunk<? extends Reservation> chunk){
        reservationRepository.saveAll(chunk.getItems());
    }
}
