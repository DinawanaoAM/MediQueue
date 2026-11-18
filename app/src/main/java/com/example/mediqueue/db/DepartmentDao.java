package com.example.mediqueue.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mediqueue.model.Department;

import java.util.List;

@Dao
public interface DepartmentDao {
    @Insert
    void insert(Department department);

    @Query("SELECT * FROM departments")
    LiveData<List<Department>> getAllDepartments();
}
