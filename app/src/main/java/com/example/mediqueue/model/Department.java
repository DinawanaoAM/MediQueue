package com.example.mediqueue.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "departments")
public class Department {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    public Department(String name) {
        this.name = name;
    }
}
