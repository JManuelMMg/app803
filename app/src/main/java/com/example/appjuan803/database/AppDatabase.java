package com.example.appjuan803.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.appjuan803.dao.CitaDao;
import com.example.appjuan803.models.Cita;

@Database(entities = {Cita.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CitaDao citaDao();
}
