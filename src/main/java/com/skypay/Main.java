package com.skypay;


import com.skypay.enums.RoomType;
import com.skypay.services.Service;

import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        Service hotel = new Service();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            hotel.setRoom(1, RoomType.STANDARD, 1000);
            hotel.setRoom(2, RoomType.JUNIOR, 2000);
            hotel.setRoom(3, RoomType.SUITE, 3000);

            hotel.setUser(1, 5000);
            hotel.setUser(2, 10000);

            hotel.bookRoom(1, 2, sdf.parse("30/06/2026"), sdf.parse("07/07/2026"));
            hotel.bookRoom(1, 2, sdf.parse("07/07/2026"), sdf.parse("30/06/2026"));
            hotel.bookRoom(1, 1, sdf.parse("07/07/2026"), sdf.parse("08/07/2026"));
            hotel.bookRoom(2, 1, sdf.parse("07/07/2026"), sdf.parse("09/07/2026"));
            hotel.bookRoom(2, 3, sdf.parse("07/07/2026"), sdf.parse("08/07/2026"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        hotel.setRoom(1, RoomType.SUITE, 10000);

        System.out.println("\n=== All Data ===");
        hotel.printAll();

        System.out.println("\n=== Users ===");
        hotel.printAllUsers();
    }
}