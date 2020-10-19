package com.example.httpserver.app.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity
public class Message {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String message;
    public Instant create;
    public Instant read;
}
