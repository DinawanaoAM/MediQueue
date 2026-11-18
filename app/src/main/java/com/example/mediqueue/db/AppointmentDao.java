package com.example.mediqueue.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mediqueue.model.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    long insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getAppointmentsForUser(int userId);

    @Query("SELECT * FROM appointments WHERE userId = :userId AND status = 'Pending' ORDER BY date ASC, time ASC LIMIT 1")
    LiveData<Appointment> getLatestPendingAppointment(int userId);

    @Query("SELECT * FROM appointments WHERE date = :date")
    LiveData<List<Appointment>> getAppointmentsByDate(String date);

    @Query("SELECT * FROM appointments WHERE date = :date AND time = :time LIMIT 1")
    Appointment getAppointmentByDateTime(String date, String time);

    @Query("SELECT * FROM appointments WHERE qrCodeData = :qrData LIMIT 1")
    Appointment getAppointmentByQr(String qrData);

    @Query("SELECT * FROM appointments ORDER BY date DESC, time DESC")
    LiveData<List<Appointment>> getAllAppointments();

    @Query("DELETE FROM appointments")
    void deleteAllAppointments();
}
