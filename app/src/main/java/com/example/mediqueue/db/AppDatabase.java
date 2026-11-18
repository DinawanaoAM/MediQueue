package com.example.mediqueue.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mediqueue.model.Announcement;
import com.example.mediqueue.model.Appointment;
import com.example.mediqueue.model.User;

import java.util.concurrent.Executors;

@Database(entities = {User.class, Appointment.class, Announcement.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract AppointmentDao appointmentDao();
    public abstract AnnouncementDao announcementDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "mediqueue_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            seedAdmin();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            seedAdmin();
        }

        private void seedAdmin() {
            Executors.newSingleThreadExecutor().execute(() -> {
                UserDao userDao = INSTANCE.userDao();
                // Check if admin exists
                User admin = userDao.login("Admin@gmail.com", "12345678");
                if (admin == null) {
                    // Try lowercase just in case
                    admin = userDao.login("admin@gmail.com", "12345678");
                }
                
                if (admin == null) {
                    userDao.insert(new User("Admin", "Admin@gmail.com", "12345678", "admin"));
                }
            });
        }
    };
}
