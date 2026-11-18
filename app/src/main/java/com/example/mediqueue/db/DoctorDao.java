package com.example.mediqueue.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mediqueue.model.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {
    @Insert
    void insert(Doctor doctor);

    @Query("SELECT * FROM doctors WHERE departmentId = :deptId")
    LiveData<List<Doctor>> getDoctorsByDepartment(int deptId);

    @Query("SELECT * FROM doctors")
    LiveData<List<Doctor>> getAllDoctors();
}
