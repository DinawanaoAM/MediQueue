package com.example.mediqueue.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mediqueue.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserById(int id);

    @Query("SELECT * FROM users WHERE role = 'patient'")
    LiveData<List<User>> getAllPatients();

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("DELETE FROM users WHERE email != 'Admin@gmail.com'")
    void deleteNonAdminUsers();
}
