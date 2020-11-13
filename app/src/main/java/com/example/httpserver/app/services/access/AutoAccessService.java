package com.example.httpserver.app.services.access;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AutoAccessService extends AccessibilityService {

    public static final String TAG = AutoAccessService.class.getSimpleName();

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "Service connected");
        Log.d(TAG, "Windows: " + getWindows());
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "Event: " + event.toString());
        try {
            Thread.sleep(12000);
            Log.d(TAG, "Window: " + event.getSource().getWindow());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service Stopped");
        return super.onUnbind(intent);
    }
}
