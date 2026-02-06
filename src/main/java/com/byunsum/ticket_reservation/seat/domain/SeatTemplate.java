package com.byunsum.ticket_reservation.seat.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Venue;
import jakarta.persistence.*;

@Table(
        name = "seat_template",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seat_template_venue_label",
                columnNames = {"venue_id", "label"}
        ),
        indexes = {
                @Index(name = "idx_seat_template_venue", columnList = "venue_id"),
                @Index(name = "idx_seat_template_venue_grade", columnList = "venue_id,grade")
        }
)
@Entity
public class SeatTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 30)
    private String label;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SeatGrade grade;

    @Column(length = 20)
    private String section;

    @Column(length = 10)
    private String rowNo;

    @Column(length = 10)
    private String colNo;

    @Column(nullable = false)
    private boolean disabled;

    public SeatTemplate() {
    }

    public SeatTemplate(Venue venue, String label, int x, int y, SeatGrade grade, String section, String rowNo, String colNo) {
        if(venue == null) {
            throw new CustomException(ErrorCode.SEAT_TEMPLATE_VENUE_REQUIRED);
        }

        if(grade == null) {
            throw new CustomException(ErrorCode.SEAT_TEMPLATE_GRADE_REQUIRED);
        }

        this.venue = venue;
        this.label = normalizedLabel(label);
        this.x = validateCoordinate(x);
        this.y = validateCoordinate(y);
        this.grade = grade;

        this.section = normalizeOptional(section);
        this.rowNo = normalizeOptional(rowNo);
        this.colNo = normalizeOptional(colNo);

        this.disabled = false;
    }

    public long getId() {
        return id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = normalizedLabel(label);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = validateCoordinate(x);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = validateCoordinate(y);
    }

    public SeatGrade getGrade() {
        return grade;
    }

    public void setGrade(SeatGrade grade) {
        if(grade == null) {
            throw new CustomException(ErrorCode.SEAT_TEMPLATE_GRADE_REQUIRED);
        }
        this.grade = grade;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = normalizeOptional(section);
    }

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = normalizeOptional(rowNo);
    }

    public String getColNo() {
        return colNo;
    }

    public void setColNo(String colNo) {
        this.colNo = normalizeOptional(colNo);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }

    private String normalizedLabel(String label) {
        if(label == null) {
            throw new CustomException(ErrorCode.SEAT_TEMPLATE_LABEL_REQUIRED);
        }

        String s = label.trim().toUpperCase();

        if(s.isBlank()) {
            throw new  CustomException(ErrorCode.SEAT_TEMPLATE_LABEL_REQUIRED);
        }

        return s;
    }

    private int validateCoordinate(int v) {
        if(v<0) {
            throw new CustomException(ErrorCode.SEAT_TEMPLATE_COORDINATE_INVALID);
        }

        return v;
    }

    private String normalizeOptional(String s) {
        if(s == null) {
            return null;
        }

        String v = s.trim();
        return v.isBlank() ? null : v;
    }
}
