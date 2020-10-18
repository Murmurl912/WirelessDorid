package com.example.httpserver.app.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.TotpRepository;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.common.handler.*;
import com.example.httpserver.common.repository.AndroidServiceConfigRepository;
import com.example.httpserver.common.server.route.Router;
import com.example.httpserver.common.server.TinyWebServer;
import com.example.httpserver.app.ui.notifications.NotificationConstants;
import com.example.httpserver.app.ui.notifications.ServerNotification;
import com.example.httpserver.common.repository.AndroidFileContextRepository;
import com.example.httpserver.common.repository.AndroidFileRepository;
import com.example.httpserver.common.service.AndroidFileService;
import com.example.httpserver.common.service.AuthService;
import com.example.httpserver.common.service.SimpleFileService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class HttpService extends Service {

    public static final String TAG = HttpService.class.getName();

    private TinyWebServer server;
    private Handler messageHandler;
    private Future<?> startup;
    private Future<?> shutdown;


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

        if(startup != null && !startup.isDone() && !startup.isCancelled()) {
            startup.cancel(true);
        }

        startup = App.app().executor().submit(()->{
            try {
                init();
                server.start();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
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
        if(startup != null && !startup.isDone() && !startup.isCancelled()) {
            startup.cancel(true);
        }

        App.app().executor().submit(()->{
            App.app().serverStatus().postValue("stopping");
            server.stop();
            App.app().serverStatus().postValue("stopped");
        });
        Log.i(TAG, "Shutdown server task submitted");
        super.onDestroy();
    }

    private AndroidFileHandler androidFileHandler() throws InvalidKeySpecException, NoSuchAlgorithmException {
        AuthService authService = new AuthService(
                new AndroidServiceConfigRepository(App.app().db().configuration()),
                TotpRepository.instance());
        SimpleFileService fileService = new SimpleFileService(new AndroidFileContextRepository(App.app().db().folder()));
        return new AndroidFileHandler(fileService, authService);
    }

    private AuthHandler authHandler() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return new AuthHandler(new AuthService(new AndroidServiceConfigRepository(App.app().db().configuration()), TotpRepository.instance()));
    }

    private void init() throws InvalidKeySpecException, NoSuchAlgorithmException {
        AndroidFileHandler handler = androidFileHandler();
        AuthHandler authHandler = authHandler();
        StaticFileStore store = new AssetsStaticFileStore(getAssets());
        StaticFileHandler staticFileHandler = new StaticFileHandler(store);
        ServerConfig config = ServerConfig.from(App.app().db().configuration().select(ServerConfig.keys));
        server.setConfig(config);
        server.router()
                .add(Router.Route.of("GET", "/api/time", (session, map) -> TinyWebServer.response(new Date().toString())))

                .add(Router.Route.of("POST", "/web-api/login", authHandler::login))
                .add(Router.Route.of("POST", "/web-api/logout", authHandler::logout))
                .add(Router.Route.of("POST", "/web-api/refresh", authHandler::refresh))

                .add(Router.Route.of("GET", "/fs-api", handler::root))
                .add(Router.Route.of("GET", "/fs-api/{context}/{*path}", handler::get))
                .add(Router.Route.of("COPY", "/fs-api/{context}/{*path}", handler::copy))
                .add(Router.Route.of("MOVE", "/fs-api/{context}/{*path}", handler::move))
                .add(Router.Route.of("PUT", "/fs-api/{context}/{*path}", handler::put))
                .add(Router.Route.of("DELETE", "/fs-api/{context}/{*path}", handler::delete))

                .add(Router.Route.of("GET", "/", staticFileHandler::index))
                .add(Router.Route.of("GET", "/web/{*path}", staticFileHandler::file));
    }


}
