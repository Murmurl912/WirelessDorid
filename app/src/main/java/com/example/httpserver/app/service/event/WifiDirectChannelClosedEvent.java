package com.example.httpserver.app.service.event;

import android.os.Bundle;

public class WifiDirectChannelClosedEvent implements ServiceEvent {
    @Override
    public int type() {
        return 0;
    }

    @Override
    public Bundle extras() {
        return null;
    }
}
