package com.skypay.services;

import com.skypay.entities.Booking;
import com.skypay.entities.Room;
import com.skypay.entities.User;
import com.skypay.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class Service {
    List<Room> rooms = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();

    Map<Integer, User> userIds = new HashMap<Integer, User>();
    Map<Integer, Room> roomIds = new HashMap<Integer, Room>();

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomIds.containsKey(roomNumber) ) {
            return; // room already exists
        }

        // saving in hashmap for quick lookup
        Room room = new Room(roomNumber, roomType, roomPricePerNight);
        roomIds.put(roomNumber, room);
        rooms.addFirst(room);
    }

    public void setUser(int userId, int balance) {
        if (userIds.containsKey(userId) ) {
            return; // user already exists
        }

        // saving in hashmap for quick lookup
        User user = new User(userId, balance);
        userIds.put(userId, user);
        users.addFirst(user);
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        if (checkOut.before(checkIn)) {
            throw new IllegalArgumentException("Invalid date range.");
        }

        User user = userIds.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found.");
        }

        Room room = roomIds.get(roomNumber);
        if (room == null) {
            throw new NoSuchElementException("Room not found.");
        }

        // Check availability
        for (Booking b : bookings) {
            if (b.getRoom().getNumber() == roomNumber &&
                    !(checkOut.before(b.getCheckIn()) || checkIn.after(b.getCheckOut()))) {
                throw new IllegalStateException("Room not available for the given period.");
            }
        }

        long nights = (checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24);
        if (nights <= 0) {
            throw new IllegalArgumentException("Booking must be at least 1 night.");
        }

        int totalPrice = (int) (nights * room.getPricePerNight());
        if (user.getBalance() < totalPrice) {
            throw new IllegalStateException("Not enough balance.");
        }

        user.setBalance( user.getBalance() - totalPrice);
        bookings.addFirst(new Booking(user, room, checkIn, checkOut));
        System.out.println("Booking successful.");
    }

    public void printAll() {
        System.out.println("Rooms:");
        for (Room room : rooms) {
            System.out.println(room);
        }
        System.out.println("Bookings:");
        for (Booking booking : bookings) {
            System.out.println(booking);
        }
    }

    public void printAllUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }
}

