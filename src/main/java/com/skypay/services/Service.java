package com.skypay.services;

import com.skypay.entities.Booking;
import com.skypay.entities.Room;
import com.skypay.entities.User;
import com.skypay.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@NoArgsConstructor
@AllArgsConstructor
public class Service {
    List<Room> rooms = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        Room existingRoom = findRoomByNumber(roomNumber);

        if (existingRoom != null) {
            existingRoom.setType(roomType);
            existingRoom.setPricePerNight(roomPricePerNight);
        } else {
            Room room = new Room(roomNumber, roomType, roomPricePerNight);
            rooms.addFirst(room);
        }
    }

    public void setUser(int userId, int balance) {
        User existingUser = findUserById(userId);

        if (existingUser != null) {
            existingUser.setBalance(balance);
        } else {
            User user = new User(userId, balance);
            users.addFirst(user);
        }
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        validateDateRange(checkIn, checkOut);

        User user = findUserById(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found.");
        }

        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            throw new NoSuchElementException("Room not found.");
        }

        checkRoomAvailability(roomNumber, checkIn, checkOut);

        long nights = calculateNights(checkIn, checkOut);
        int totalPrice = (int) (nights * room.getPricePerNight());

        chargeUser(user, totalPrice);

        Booking booking = new Booking(
                new User(user.getId(), user.getBalance()),
                new Room(room.getNumber(), room.getType(), room.getPricePerNight()),
                checkIn,
                checkOut
        );

        bookings.addFirst(booking);
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

    private User findUserById(int userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElse(null);
    }

    private Room findRoomByNumber(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getNumber() == roomNumber)
                .findFirst()
                .orElse(null);
    }

    private void validateDateRange(Date checkIn, Date checkOut) {
        if (checkOut.before(checkIn)) {
            throw new IllegalArgumentException("Invalid date range.");
        }
    }

    private void checkRoomAvailability(int roomNumber, Date checkIn, Date checkOut) {
        boolean isUnavailable = bookings.stream().anyMatch(booking ->
                booking.getRoom().getNumber() == roomNumber &&
                        !(checkOut.before(booking.getCheckIn()) || checkIn.after(booking.getCheckOut()))
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
