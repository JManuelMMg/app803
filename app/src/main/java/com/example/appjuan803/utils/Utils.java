package com.example.appjuan803.utils;

import android.content.Context;
import androidx.room.Room;
import com.example.appjuan803.database.AppDatabase;

public class Utils {
    private static AppDatabase database;

    public static AppDatabase getDatabase(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "AppJuan803_db"
            ).build();
        }
        return database;
    }
}

