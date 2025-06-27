package com.skypay.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private int balance;

    @Override
    public String toString() {
        return "User{id=" + id + ", balance=" + balance + "}";
    }
}

