package com.example.mediqueue.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String patientName;
    public String service; // General Checkup, Prenatal, Immunization, Dental
    public String date;
    public String time;
    public String status; // "Pending", "Completed"
    public String qrCodeData;

    public Appointment(int userId, String patientName, String service, String date, String time, String status, String qrCodeData) {
        this.userId = userId;
        this.patientName = patientName;
        this.service = service;
        this.date = date;
        this.time = time;
        this.status = status;
        this.qrCodeData = qrCodeData;
    }
}
