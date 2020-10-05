package com.example.httpserver.app;

import android.app.Application;
import androidx.room.Room;
import com.example.httpserver.app.database.AppDatabase;

public class App extends Application {

    public static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
    }

    public static AppDatabase db() {
        return db;
    }

}
