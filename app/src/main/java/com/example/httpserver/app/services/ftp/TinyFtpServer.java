package com.example.httpserver.app.services.ftp;

import com.example.httpserver.app.services.ServiceConfigurationRepository;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UserFactory;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class TinyFtpServer {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_HOME_DIR = "home_dir";
    public static final String KEY_ENABLED = "user_enabled";
    public static final String KEY_WRITE = "write";
    public static final String KEY_CONCURRENT = "concurrent";
    public static final String KEY_FTP_PORT = "ftp_port";
    public static final String KEY_FTP_TIME_OUT = "ftp_default_timeout";
    public static final String DEFAULT_USERNAME = null;
    public static final String DEFAULT_PASSWORD = null;
    public static final String DEFAULT_HOME_DIR = "";
    public static final String DEFAULT_ENABLED = "true";
    public static final String DEFAULT_WRITE = "true";
    public static final String DEFAULT_CONCURRENT = "true";
    public static final String DEFAULT_CONCURRENT_LOGIN = "4";
    public static final String DEFAULT_CONCURRENT_LOGIN_PER_IP = "4";
    public static final String DEFAULT_FTP_PORT = "2121";
    public static final String DEFAULT_FTP_TIME_OUT = "600";
    private final BiConsumer<Integer, Exception> DEFAULT_LISTENER = (integer, e) -> {

    };
    private FtpServer server;
    private ServiceConfigurationRepository repository;
    private BiConsumer<Integer, Exception> listener = DEFAULT_LISTENER;

    public TinyFtpServer(ServiceConfigurationRepository repository) {
        this.repository = repository;
    }

    private User initUser() {
        UserFactory userFactory = new UserFactory();
        userFactory.setHomeDirectory(repository.get(KEY_HOME_DIR, DEFAULT_USERNAME));
        userFactory.setName(repository.get(KEY_USERNAME, DEFAULT_USERNAME));
        userFactory.setPassword(repository.get(KEY_PASSWORD, DEFAULT_PASSWORD));
        userFactory.setEnabled(Boolean.parseBoolean(repository.get(KEY_ENABLED, DEFAULT_ENABLED)));
        ArrayList<Authority> authorities = new ArrayList<>();
        if (Boolean.parseBoolean(repository.get(KEY_WRITE, DEFAULT_WRITE))) {
            authorities.add(new WritePermission());
        }
        if (Boolean.parseBoolean(repository.get(KEY_CONCURRENT, DEFAULT_CONCURRENT))) {
            authorities.add(new ConcurrentLoginPermission(4, 4));
        }
        userFactory.setAuthorities(authorities);
        return userFactory.createUser();
    }

    private ConnectionConfig initConnectionConfig() {
        ConnectionConfigFactory factory = new ConnectionConfigFactory();
        factory.setMaxLogins(8);
        factory.setMaxThreads(16);
        factory.setAnonymousLoginEnabled(true);
        factory.setMaxLoginFailures(5);
        factory.setLoginFailureDelay(10);
        factory.setAnonymousLoginEnabled(true);
        return factory.createConnectionConfig();
    }

    private Listener initListener() {
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(Integer.parseInt(repository.get(KEY_FTP_PORT, DEFAULT_FTP_PORT)));
        factory.setIdleTimeout(Integer.parseInt(repository.get(KEY_FTP_TIME_OUT, DEFAULT_FTP_TIME_OUT)));
        return factory.createListener();
    }

    private void init() {
        FtpServerFactory factory = new FtpServerFactory();
        User user = initUser();
        try {
            factory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
            // todo handle exception
        }
        factory.setConnectionConfig(initConnectionConfig());
        factory.addListener("default", initListener());
        server = factory.createServer();
    }

    public synchronized void start() {
        try {
            listener.accept(0, null);
            init();
            server.start();
            listener.accept(1, null);
        } catch (FtpException e) {
            e.printStackTrace();
            listener.accept(-1, e);
            // todo handle exception
        }
    }

    public synchronized void stop() {
        if (server != null) {
            listener.accept(2, null);
            server.stop();
            server = null;
        }
        listener.accept(3, null);
    }

    public void setListener(BiConsumer<Integer, Exception> listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }
}
