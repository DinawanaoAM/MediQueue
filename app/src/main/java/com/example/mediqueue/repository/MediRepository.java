package com.example.mediqueue.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mediqueue.db.AnnouncementDao;
import com.example.mediqueue.db.AppDatabase;
import com.example.mediqueue.db.AppointmentDao;
import com.example.mediqueue.db.UserDao;
import com.example.mediqueue.model.Announcement;
import com.example.mediqueue.model.Appointment;
import com.example.mediqueue.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediRepository {
    private final UserDao userDao;
    private final AppointmentDao appointmentDao;
    private final AnnouncementDao announcementDao;
    private final ExecutorService executorService;

    public MediRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        appointmentDao = db.appointmentDao();
        announcementDao = db.announcementDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public void insertUser(User user, OnResultListener<Long> listener) {
        executorService.execute(() -> {
            long id = userDao.insert(user);
            if (listener != null) listener.onResult(id);
        });
    }

    public void login(String email, String password, OnResultListener<User> listener) {
        executorService.execute(() -> {
            User user = userDao.login(email, password);
            if (listener != null) listener.onResult(user);
        });
    }

    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void bookAppointment(Appointment appointment, OnResultListener<String> listener) {
        executorService.execute(() -> {
            Appointment existing = appointmentDao.getAppointmentByDateTime(appointment.date, appointment.time);
            if (existing != null) {
                if (listener != null) listener.onResult("Schedule already booked");
            } else {
                appointmentDao.insert(appointment);
                if (listener != null) listener.onResult("Success");
            }
        });
    }

    public LiveData<List<Appointment>> getAppointmentsForUser(int userId) {
        return appointmentDao.getAppointmentsForUser(userId);
    }

    public LiveData<Appointment> getLatestPendingAppointment(int userId) {
        return appointmentDao.getLatestPendingAppointment(userId);
    }

    public LiveData<List<Appointment>> getAllAppointments() {
        return appointmentDao.getAllAppointments();
    }

    public void updateAppointment(Appointment appointment) {
        executorService.execute(() -> appointmentDao.update(appointment));
    }

    public void getAppointmentByQr(String qrData, OnResultListener<Appointment> listener) {
        executorService.execute(() -> {
            Appointment appointment = appointmentDao.getAppointmentByQr(qrData);
            if (listener != null) listener.onResult(appointment);
        });
    }

    public LiveData<List<Announcement>> getAllAnnouncements() {
        return announcementDao.getAllAnnouncements();
    }

    public void insertAnnouncement(Announcement announcement) {
        executorService.execute(() -> announcementDao.insert(announcement));
    }

    public void deleteAnnouncement(Announcement announcement) {
        executorService.execute(() -> announcementDao.delete(announcement));
    }

    public LiveData<List<Appointment>> getAppointmentsForDate(String date) {
        return appointmentDao.getAppointmentsByDate(date);
    }

    public LiveData<List<User>> getAllPatients() {
        return userDao.getAllPatients();
    }

    public interface OnResultListener<T> {
        void onResult(T result);
    }
}
