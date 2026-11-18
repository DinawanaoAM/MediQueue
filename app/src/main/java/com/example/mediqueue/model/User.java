package com.example.mediqueue.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String birthdate;
    public String address;
    public String phoneNumber;
    public String email;
    public String password;
    public String role; // "patient" or "admin"
    public String healthInfo;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
