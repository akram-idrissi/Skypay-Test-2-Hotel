package com.skypay.entities;

import com.skypay.enums.RoomType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private int number;
    private RoomType type;
    private int pricePerNight;

    @Override
    public String toString() {
        return "Room{number=" + number + ", type=" + type + ", price/night=" + pricePerNight + "}";
    }
}


