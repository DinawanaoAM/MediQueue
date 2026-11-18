package com.example.mediqueue.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors")
public class Doctor {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int departmentId;
    public String availability; // Simple string for now, e.g., "Mon-Fri 9-5"

    public Doctor(String name, int departmentId, String availability) {
        this.name = name;
        this.departmentId = departmentId;
        this.availability = availability;
    }
}
