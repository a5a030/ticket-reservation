package com.byunsum.ticket_reservation.batch.job.reservation.reader;

import com.byunsum.ticket_reservation.reservation.batch.FakeReservationGenerator;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

public class FakeReservationReader implements ItemReader<ReservationRequest> {
    private final Iterator<ReservationRequest> iterator;

    public FakeReservationReader(List<ReservationRequest> requests) {
        this.iterator = requests.iterator();
    }

    public FakeReservationReader() {
        FakeReservationGenerator generator = new FakeReservationGenerator(100, 10, 1000);
        List<ReservationRequest> requests = generator.generate(500);
        this.iterator = requests.iterator();
    }

    @Override
    public ReservationRequest read(){
        return iterator.hasNext() ? iterator.next() : null;
    }
}
