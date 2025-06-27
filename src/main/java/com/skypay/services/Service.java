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

    // Using hashmaps for quick lookup
    Map<Integer, User> userIds = new HashMap<>();
    Map<Integer, Room> roomIds = new HashMap<>();

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
        validateDateRange(checkIn, checkOut);

        User user = this.findUser(userId);
        Room room = this.findRoom(roomNumber);

        checkRoomAvailability(roomNumber, checkIn, checkOut);

        long nights = calculateNights(checkIn, checkOut);
        int totalPrice = (int) (nights * room.getPricePerNight());

        this.chargeUser(user, totalPrice);

        bookings.addFirst(new Booking(user, room, checkIn, checkOut));
        System.out.println("Booking successful.");
    }

    public void printAll() {
        System.out.println("Rooms:");
        rooms.forEach(System.out::println);

        System.out.println("--------------------");

        System.out.println("Bookings:");
        bookings.forEach(System.out::println);
    }

    public void printAllUsers() {
        users.forEach(System.out::println);
    }

    private void validateDateRange(Date checkIn, Date checkOut) {
        if (checkOut.before(checkIn)) {
            throw new IllegalArgumentException("Invalid date range.");
        }
    }

    private User findUser(int userId) {
        User user = userIds.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found.");
        }
        return user;
    }

    private Room findRoom(int roomNumber) {
        Room room = roomIds.get(roomNumber);
        if (room == null) {
            throw new NoSuchElementException("Room not found.");
        }
        return room;
    }

    private void checkRoomAvailability(int roomNumber, Date checkIn, Date checkOut) {
        boolean isUnavailable = bookings.stream().anyMatch(b ->
                b.getRoom().getNumber() == roomNumber &&
                        !(checkOut.before(b.getCheckIn()) || checkIn.after(b.getCheckOut()))
        );

        if (isUnavailable) {
            throw new IllegalStateException("Room not available for the given period - " +
                    "From: " + checkIn + ", To: " + checkOut);
        }
    }

    private long calculateNights(Date checkIn, Date checkOut) {
        long diffMillis = checkOut.getTime() - checkIn.getTime();
        long nights = diffMillis / (1000 * 60 * 60 * 24);
        if (nights <= 0) {
            throw new IllegalArgumentException("Booking must be at least 1 night.");
        }
        return nights;
    }

    private void chargeUser(User user, int amount) {
        if (user.getBalance() < amount) {
            throw new IllegalStateException("Not enough balance for user id: " + user.getId());
        }
        user.setBalance(user.getBalance() - amount);
    }

}

