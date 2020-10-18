package com.example.httpserver.app.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.function.BiConsumer;

public class HttpService extends Service {

    public static final String TAG = HttpService.class.getName();

    private TinyWebServer server;
    private AndroidFileHandler handler;
    private Handler messageHandler;
    private StaticFileStore store;
    private StaticFileHandler staticFileHandler;
    private AuthHandler authHandler;

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
                Log.i(TAG, "Creating handler");
                handler = androidFileHandler();
                authHandler = authHandler();
                store = new AssetsStaticFileStore(getAssets());
                staticFileHandler = new StaticFileHandler(store);
                Log.i(TAG, "Handler created");
                server.setConfig(config);
                Log.i(TAG, "Setup server routes");
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
                        .add(Router.Route.of("GET", "/web/{*path}", staticFileHandler::file))

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

}
