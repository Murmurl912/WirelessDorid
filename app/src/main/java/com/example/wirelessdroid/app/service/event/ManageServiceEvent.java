package com.example.wirelessdroid.app.service.event;

import android.os.Bundle;

public enum ManageServiceEvent implements ServiceEvent {
    MANAGE_SERVICE_STARTUP(null),
    MANAGE_SERVICE_START_TIMEOUT(null),
    MANAGE_SERVICE_SHUTDOWN(null),
    MANAGE_SERVICE_STOP_TIMEOUT(null),
    ;
    private Bundle extras;
    ManageServiceEvent(Bundle extras) {
        this.extras = extras;
    }

    @Override
    public Bundle extras(Bundle bundle) {
        Bundle old = this.extras;
        this.extras = bundle;
        return old;
    }
}
