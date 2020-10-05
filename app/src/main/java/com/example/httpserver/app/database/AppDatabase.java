package com.example.httpserver.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.httpserver.app.adapter.repository.FolderRepository;
import com.example.httpserver.app.adapter.repository.entity.Folder;

@Database(entities = {Folder.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FolderRepository getFolderRepository();
}