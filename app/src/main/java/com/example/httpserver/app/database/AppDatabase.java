package com.example.httpserver.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.httpserver.app.repository.ConfigurationRepository;
import com.example.httpserver.app.repository.entity.Configuration;

@Database(entities = {Configuration.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConfigurationRepository configuration();

}