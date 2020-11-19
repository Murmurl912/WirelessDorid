package com.example.httpserver.app.repository.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity
public class Device {
    @PrimaryKey
    public String address;

    public String name;

    public DeviceType type;
    public DeviceStatus status;

    public Instant create;
    public Instant active;
    public Instant duration;


    public static enum DeviceStatus {
        STATUS_BLOCKED,
        STATUS_NORMAL,
        STATUS_READONLY
    }

    public static enum DeviceType {

    }
}
