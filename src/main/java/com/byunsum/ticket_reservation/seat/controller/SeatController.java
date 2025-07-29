package com.byunsum.ticket_reservation.seat.controller;

import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.service.SeatSelectionService;
import com.byunsum.ticket_reservation.seat.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/select/status")
    public ResponseEntity<String> getSeatSelectionStatus(@RequestParam Long seatId) {
        String selectedBy = seatSelectionService.getSeatStatus(seatId);

        return ResponseEntity.ok(selectedBy != null ? selectedBy : "AVAILABLE");
    }

    @DeleteMapping("/select")
    public ResponseEntity<Void> cancelSeatSelection(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatSelectionService.cancelSelection(seatId, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> confirmReservation(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatService.confirmReservation(seatId, memberId);
        return ResponseEntity.ok().build();
    }

}
