package com.example.httpserver.app.services.ftp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.httpserver.app.App;
import com.example.httpserver.app.services.AndroidServiceConfigurationRepository;
import com.example.httpserver.app.services.ServiceConfigurationRepository;
import org.greenrobot.eventbus.EventBus;

import java.util.function.BiConsumer;

public class FtpService extends Service implements Runnable {

    private Thread thread;
    private TinyFtpServer server;
    private ServiceConfigurationRepository repository;

    private final BiConsumer<Integer, Exception> listener = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception e) {
            EventBus.getDefault().post("Ftp Service: " + integer + ", " + e.toString());
        }
    };

    @Nullable
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
        if(thread.isAlive()) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        thread = null;
        if(server != null) {
            server.stop();
            server = null;
        }
        super.onDestroy();
    }

    @Override
    public void run() {
        if(server != null) {
            server.stop();
        }
        server = new TinyFtpServer(repository);
        server.setListener(listener);
        server.start();
    }
}
