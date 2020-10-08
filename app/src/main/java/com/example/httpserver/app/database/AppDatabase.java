package com.example.httpserver.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.httpserver.app.repository.ConfigurationRepository;
import com.example.httpserver.app.repository.FolderRepository;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.Folder;

@Database(entities = {Folder.class, Configuration.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FolderRepository folder();

    public abstract ConfigurationRepository configuration();

}