package com.skypay.entities;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private User user;
    private Room room;
    private Date checkIn;
    private Date checkOut;

    @Override
    public String toString() {
        return "Booking{user=" + user +
                ", room=" + room +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut + "}";
    }
}
