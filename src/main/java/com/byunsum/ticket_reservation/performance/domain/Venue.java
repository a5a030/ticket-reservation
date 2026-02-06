package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import jakarta.persistence.*;

@Table(
        indexes = {
                @Index(name = "idx_venue_name", columnList = "name")
        }
)
@Entity
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(length = 200)
    private String address;

    @Column(nullable = false)
    private int mapWidth;

    @Column(nullable = false)
    private int mapHeight;

    public Venue() {
    }

    public Venue(String name, String city, String address, int mapWidth, int mapHeight) {
        this.name = normalize(name, ErrorCode.VENUE_NAME_REQUIRED);
        this.city = normalize(city, ErrorCode.VENUE_CITY_REQUIRED);
        this.address = normalizeOptional(address);
        this.mapWidth = validateMapSize(mapWidth);
        this.mapHeight = validateMapSize(mapHeight);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name, ErrorCode.VENUE_NAME_REQUIRED);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = normalize(city, ErrorCode.VENUE_CITY_REQUIRED);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = normalizeOptional(address);
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = validateMapSize(mapWidth);
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = validateMapSize(mapHeight);
    }

    private String normalize(String value, ErrorCode errorCode) {
        if(value == null) {
            throw new CustomException(errorCode);
        }

        String trimmed = value.trim();

        if(trimmed.isBlank()) {
            throw new CustomException(errorCode);
        }

        return trimmed;
    }

    private String normalizeOptional(String value) {
        if(value == null) {
            return null;
        }

        String s = value.trim();

        return s.isBlank() ? null : s;
    }

    private int validateMapSize(int v) {
        if(v <= 0) {
            throw new CustomException(ErrorCode.VENUE_MAP_SIZE_INVALID);
        }

        return v;
    }
}
