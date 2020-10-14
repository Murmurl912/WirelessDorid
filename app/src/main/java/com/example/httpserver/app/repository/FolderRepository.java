package com.example.httpserver.app.repository;

import androidx.room.*;

import com.example.httpserver.app.repository.entity.Folder;

import java.util.List;
import java.util.Objects;

@Dao
public interface FolderRepository {

    @Insert
    public long[] add(Folder...folders);
    @Delete
    public void remove(Folder...path);
    @Update
    public void update(Folder...folder);

    @Query("select * from folder")
    public List<Folder> gets();

    @Query("select * from folder where id = :id")
    public Folder getById(long id);

    @Query("select count(*) from folder where path = :path")
    public boolean containPath(String path);

    @Query("select * from folder where path = :path")
    public Folder getByPath(String path);

    @Query("select * from folder where name = :name")
    public Folder getByName(String name);

    @Query("select folder.name from folder")
    public List<String> names();

    @Query("select count(name) from folder where name = :name")
    public boolean containName(String name);

    @Query("select count(id) from folder where id = :id")
    public boolean contains(Long id);

    @Query("select count(name) from folder where name = :name")
    public int countName(String name);

    @Query("select count(path) from folder where path = :path")
    public int countPath(String path);

    @Query("select count(id) from folder where id = :id")
    public int countId(Long id);

    @Transaction
    public default long save(Folder folder) {
        if(countId(folder.id) > 0) {
            Folder old = getById(folder.id);
            if(!Objects.equals(folder.name, old.name)) {
                if(countName(folder.name) > 0) {
                    throw new IllegalStateException();
                }
            }
            if(!Objects.equals(folder.path, old.path)) {
                if(countPath(folder.path) > 0) {
                    throw new IllegalStateException();
                }
            }
            update(folder);
            return folder.id;
        } else {
            if(countName(folder.name) > 0) {
                throw new IllegalStateException();
            }
            if(countPath(folder.path) > 0) {
                throw new IllegalStateException();
            }
            long[] ids = add(folder);
            return ids[0];
        }
    }
}
