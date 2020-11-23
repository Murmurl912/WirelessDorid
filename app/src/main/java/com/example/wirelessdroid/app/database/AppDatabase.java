package com.example.wirelessdroid.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.wirelessdroid.app.repository.ConfigurationRepository;
import com.example.wirelessdroid.app.repository.entity.Configuration;

@Database(entities = {Configuration.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConfigurationRepository configuration();

}