package com.example.wirelessdroid.app;

import android.app.Application;
import android.util.Log;
import androidx.room.Room;
import com.example.wirelessdroid.app.database.AppDatabase;
import com.example.wirelessdroid.app.service.event.ServiceEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static final String TAG = App.class.getSimpleName();
    private static App app;
    private AppDatabase db;
    private ExecutorService executor;

    public static App app() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        setup();
        app = this;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    private void setup() {
        Date date = new Date();
        Log.i(TAG, "start setup app executor and database at " + date);
        executor = Executors.newCachedThreadPool();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();

        Log.i(TAG, "finish setup app executor and database at " + new Date() + ", duration: " + (System.currentTimeMillis() - date.getTime()) + "ms");
    }

    public AppDatabase db() {
        return db;
    }

    public ExecutorService executor() {
        return executor;
    }

    @Subscribe
    public void log(ServiceEvent event) {
        Log.d(TAG, event.toString());
    }
}
