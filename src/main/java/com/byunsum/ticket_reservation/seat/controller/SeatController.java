package com.byunsum.ticket_reservation.seat.controller;

import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    public ResponseEntity<Void> createSeat(@RequestBody SeatRequest request) {
        seatService.createSeat(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
