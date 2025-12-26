package com.example.oya.employeedb;

import androidx.room.PrimaryKey;
import androidx.room.Entity;


@Entity
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public int salary;
}
