package com.example.httpserver.app.service.http;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.example.httpserver.app.App;
import com.example.httpserver.app.service.AndroidServiceConfigurationRepository;
import com.example.httpserver.app.service.ServiceConfigurationRepository;
import org.greenrobot.eventbus.EventBus;

import java.util.function.BiConsumer;

public class HttpService extends Service implements Runnable {

    public static final String TAG = HttpService.class.getName();
    private final BiConsumer<Integer, Exception> listener = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {
            EventBus.getDefault().post("Http Service: " + integer + ", " + exception.toString());
        }
    };
    private TinyHttpServer server;
    private ServiceConfigurationRepository repository;
    private Thread thread;

    public HttpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new AndroidServiceConfigurationRepository(App.app().db().configuration());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        thread = null;
        if (server != null) {
            server.stop();
            server = null;
        }
        super.onDestroy();
    }

    @Override
    public void run() {
        if (server != null) {
            server.stop();
        }
        server = new TinyHttpServer(repository, getAssets());
        server.setServerListener(listener);
    }
}
