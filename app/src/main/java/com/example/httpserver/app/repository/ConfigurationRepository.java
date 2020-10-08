package com.example.httpserver.app.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.httpserver.app.repository.entity.Configuration;

import java.util.List;

@Dao
public interface ConfigurationRepository {
    @Insert
    public void insert(Configuration...configurations);

    @Update
    public void update(Configuration...configurations);

    @Delete
    public void delete(Configuration...configurations);

    @Query("select * from configuration")
    public Configuration select();

    @Query("select * from configuration where `key` in (:keys)")
    public List<Configuration> select(String...keys);

    @Query("select count(*) from configuration where `key` = :key")
    public int contains(String key);

    @Transaction
    default public void save(Configuration...configurations) {
        for (Configuration configuration : configurations) {
            if(contains(configuration.key) > 0) {
                update(configuration);
            } else {
                insert(configuration);
            }
        }
    }

    default public void save(List<Configuration> configurations) {
        for (Configuration configuration : configurations) {
            if(contains(configuration.key) > 0) {
                update(configuration);
            } else {
                insert(configuration);
            }
        }
    }
}
