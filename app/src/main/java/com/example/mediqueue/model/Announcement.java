package com.example.mediqueue.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "announcements")
public class Announcement {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String content;
    public long timestamp;

    public Announcement(String title, String content, long timestamp) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }
}
