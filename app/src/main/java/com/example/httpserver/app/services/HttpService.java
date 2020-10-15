package com.example.httpserver.app.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.http.handler.AssetsStaticFileStore;
import com.example.httpserver.app.services.http.handler.FileHandler;
import com.example.httpserver.app.services.http.handler.StaticFileHandler;
import com.example.httpserver.app.services.http.handler.StaticFileStore;
import com.example.httpserver.app.services.http.server.TinyWebServer;
import com.example.httpserver.app.services.http.route.Router;
import com.example.httpserver.app.ui.notifications.NotificationConstants;
import com.example.httpserver.app.ui.notifications.ServerNotification;
import com.example.httpserver.service.AndroidFileService;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.Date;
import java.util.function.BiConsumer;

public class HttpService extends Service {

    public static final String TAG = HttpService.class.getName();

    private TinyWebServer server;
    private FileHandler handler;
    private Handler messageHandler;
    private StaticFileStore store;
    private StaticFileHandler staticFileHandler;

    private final BiConsumer<Integer, Exception> listener = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {
            Log.i(TAG, "Server state: " + integer, exception);
            switch (integer) {
                case TinyWebServer.STATE_STOPPED:
                    messageHandler.post(()->{
                        Toast.makeText(getApplication(), "Server is stopped", Toast.LENGTH_SHORT).show();;
                    });
                    break;
                case TinyWebServer.STATE_STARTING:
                    messageHandler.post(()->{
                        Toast.makeText(getApplication(), "Server is starting", Toast.LENGTH_SHORT).show();;
                    });
                    break;
                case TinyWebServer.STATE_RUNNING:
                    messageHandler.post(()->{
                        Toast.makeText(getApplication(), "Server is running", Toast.LENGTH_SHORT).show();;
                    });
                    break;
                case TinyWebServer.STATE_STOPPING:
                    messageHandler.post(()->{
                        Toast.makeText(getApplication(), "Server is stopping", Toast.LENGTH_SHORT).show();;
                    });
                    break;
                case TinyWebServer.STATE_ERROR:
                    messageHandler.post(()->{
                        Toast.makeText(getApplication(), "Server Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();;
                    });
                    break;
                    
            }
        }
    };

    public HttpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Http service started");
        App.app().executor().submit(()->{
            try {
                App.app().serverStatus().postValue("starting");
                Log.i(TAG, "Loading server config from database");
                ServerConfig config = ServerConfig.from(App.app().db().configuration().select(ServerConfig.keys));
                Log.i(TAG, "Server config: " + config);
                Log.i(TAG, "Creating file handler");
                handler = new FileHandler(new AndroidFileService(), App.app().db().folder());
                Log.i(TAG, "File handler created");
                Log.i(TAG, "Creating static file handler and store");
                store = new AssetsStaticFileStore(getAssets());
                staticFileHandler = new StaticFileHandler(store);
                Log.i(TAG, "Static file handler and store created");
                server.setConfig(config);
                Log.i(TAG, "Setup server routes");
                server.router()
                        .add(Router.Route.of("GET", "/api/time", (session, map) -> TinyWebServer.response(new Date().toString())))
                        .add(Router.Route.of("GET", "/{context}/{*path}", handler::get))
                        .add(Router.Route.of("PUT", "/{context}/{*path}", handler::put))
                        .add(Router.Route.of("DELETE", "/{context}/{*path}", handler::delete))
                        .add(Router.Route.of("GET", "/", staticFileHandler::index))
                        .add(Router.Route.of("GET", "/static/{*path}", staticFileHandler::file))

                ;
                Log.i(TAG, "Server routes setup completed");
                Log.i(TAG, "Starting server");
                server.start();
                Log.i(TAG, "Server started");
                App.app().serverStatus().postValue("running");
            } catch (Exception e) {
                App.app().serverStatus().postValue("error");
                Log.e(TAG, "Failed to start http server", e);
            }
        });
        Log.i(TAG, "Http server startup task submitted");

        Log.i(TAG, "Start service in foreground");
        Notification notification =
                ServerNotification.startNotification(getApplicationContext(), false);
        startForeground(NotificationConstants.SERVER_ID, notification);
        Log.i(TAG, "Service running in foreground");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Http Service created");
        messageHandler = new Handler(getMainLooper());
        server = new TinyWebServer();
        server.setServerListener(listener);
    }

    @Override
    public void onDestroy() {

        App.app().executor().submit(()->{
            App.app().serverStatus().postValue("stopping");
            server.stop();
            App.app().serverStatus().postValue("stopped");
        });
        Log.i(TAG, "Shutdown server task submitted");
        super.onDestroy();
    }


}
