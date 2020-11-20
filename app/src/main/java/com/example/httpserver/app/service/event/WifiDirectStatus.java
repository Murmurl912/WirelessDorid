package com.example.httpserver.app.service.event;

import android.os.Bundle;

public enum WifiDirectStatus implements ServiceEvent {
    STATUS_ENABLED(null),
    STATUS_OPENING(null),
    STATUS_OPENED(null),
    STATUS_STARTING(null),
    STATUS_STARTED(null),
    STATUS_ERROR(null),
    STATUS_CLOSING(null),
    STATUS_CLOSED(null),
    STATUS_DISABLED(null)
    ;

    private Bundle extras;

    WifiDirectStatus(Bundle extras) {
        this.extras = extras;
    }
    @Override
    public Bundle extras(Bundle bundle) {
        return null;
    }
}
