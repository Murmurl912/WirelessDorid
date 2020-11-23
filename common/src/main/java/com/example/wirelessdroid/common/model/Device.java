package com.example.wirelessdroid.common.model;

import java.security.KeyPair;
import java.time.Instant;

public class Device {

    public String id;
    public String name;
    public String nickname;

    public DeviceStatus status;
    public DeviceType type;

    public Instant create;
    public Instant active;
    public Instant duration;

    public static enum DeviceType {
        DEVICE_TYPE_PC,
        DEVICE_TYPE_PHONE,
        DEVICE_TYPE_UNKNOWN
    }

    public static enum  DeviceStatus {
        DEVICE_STATUS_BLOCKED,
        DEVICE_STATUS_NORMAL,
        ;
    }
}
