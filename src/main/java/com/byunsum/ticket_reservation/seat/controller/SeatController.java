package com.byunsum.ticket_reservation.seat.controller;

import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.service.SeatSelectionService;
import com.byunsum.ticket_reservation.seat.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/seats")
public class SeatController {
    private final SeatService seatService;
    private final SeatSelectionService seatSelectionService;

    public SeatController(SeatService seatService, SeatSelectionService seatSelectionService) {
        this.seatService = seatService;
        this.seatSelectionService = seatSelectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createSeat(@RequestBody SeatRequest request) {
        seatService.createSeat(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/select")
    public ResponseEntity<Void> selectSeat(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatSelectionService.selectSeat(seatId, memberId);

        return ResponseEntity.ok().build();
    }
}
