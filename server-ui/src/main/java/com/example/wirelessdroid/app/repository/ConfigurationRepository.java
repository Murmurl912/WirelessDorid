package com.example.wirelessdroid.app.repository;

import androidx.room.*;
import com.example.wirelessdroid.app.repository.entity.Configuration;

import java.util.List;

@Dao
public interface ConfigurationRepository {
    @Insert
    public void insert(Configuration... configurations);

    @Update
    public void update(Configuration... configurations);

    @Delete
    public void delete(Configuration... configurations);

    @Query("select * from configuration")
    public Configuration gets();

    @Query("select value from configuration where `key` = :key")
    public String get(String key);

    default public void put(String key, String value) {
        Configuration configuration = new Configuration(key, value);
        save(configuration);
    }

    @Query("select * from configuration where `key` in (:keys)")
    public List<Configuration> select(String... keys);

    @Query("select count(*) from configuration where `key` = :key")
    public int contains(String key);

    @Transaction
    default public void save(Configuration... configurations) {
        for (Configuration configuration : configurations) {
            if (contains(configuration.key) > 0) {
                update(configuration);
            } else {
                insert(configuration);
            }
        }
    }

    default public void save(List<Configuration> configurations) {
        for (Configuration configuration : configurations) {
            if (contains(configuration.key) > 0) {
                update(configuration);
            } else {
                insert(configuration);
            }
        }
    }
}
