package com.example.httpserver.app;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import com.example.httpserver.app.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static AppDatabase db;
    public static LiveData<Boolean> serverRunning = new MutableLiveData<>(false);
    public static ExecutorService executor = Executors.newCachedThreadPool();

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
