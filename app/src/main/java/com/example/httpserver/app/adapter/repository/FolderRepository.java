package com.example.httpserver.app.adapter.repository;

import androidx.room.*;
import com.example.httpserver.app.adapter.repository.entity.Folder;

import java.util.List;

@Dao
public interface FolderRepository {

    @Insert
    public void add(Folder...folders);
    @Delete
    public void remove(Folder...path);
    @Update
    public void update(Folder...folder);

    @Query("select * from folder")
    public List<Folder> get();

    @Query("select count(*) from folder where path = :path")
    public boolean contain(String path);

    @Query("select * from folder where path = :path")
    public Folder get(String path);
}
