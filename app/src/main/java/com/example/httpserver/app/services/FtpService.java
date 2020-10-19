package com.example.httpserver.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.httpserver.app.services.ftp.TinyFtpServer;
import com.example.httpserver.common.server.TinyWebServer;

import java.util.function.BiConsumer;

public class FtpService extends Service implements Runnable {

    private Thread thread;
    private TinyFtpServer server;
    private ServiceConfigurationRepository repository;

    private final BiConsumer<Integer, Exception> listener = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception e) {

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
        server = new TinyFtpServer(repository);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(thread.isAlive()) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
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
        server.setListener(listener);
        server.start();
    }
}
