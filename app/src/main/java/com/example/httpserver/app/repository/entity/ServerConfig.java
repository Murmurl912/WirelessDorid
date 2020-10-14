package com.example.httpserver.app.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig implements Parcelable {
    public int http_port = DEFAULT_HTTP_PORT;
    public int ftp_port = DEFAULT_FTP_PORT;
    public String address = "";
    public boolean http = true;
    public boolean ftp = false;
    public boolean basic = false;
    public boolean totp = true;
    public boolean tls = true;
    public String key = "default";
    public String cert = "default";
    public String username = "";
    public String password = "";
    public String status = "stopped";

    public static int DEFAULT_HTTP_PORT = 8080;
    public static int DEFAULT_FTP_PORT = 6060;

    public static final String[] keys = {
            "http_port",
            "ftp_port",
            "http",
            "ftp",
            "address",
            "basic",
            "totp",
            "tls",
            "totp_key",
            "tls_cert",
            "username",
            "password",
            "status"
    };

    public ServerConfig() {

    }

    protected ServerConfig(Parcel in) {
        http_port = in.readInt();
        ftp_port = in.readInt();
        address = in.readString();
        http = in.readByte() != 0;
        ftp = in.readByte() != 0;
        basic = in.readByte() != 0;
        totp = in.readByte() != 0;
        tls = in.readByte() != 0;
        key = in.readString();
        cert = in.readString();
        username = in.readString();
        password = in.readString();
        status = in.readString();
    }

    public static final Creator<ServerConfig> CREATOR = new Creator<ServerConfig>() {
        @Override
        public ServerConfig createFromParcel(Parcel in) {
            return new ServerConfig(in);
        }

        @Override
        public ServerConfig[] newArray(int size) {
            return new ServerConfig[size];
        }
    };

    public List<Configuration> to() {
        ArrayList<Configuration> configurations = new ArrayList<>();
        configurations.add(new Configuration("ftp", ftp + ""));
        configurations.add(new Configuration("http", http + ""));
        configurations.add(new Configuration("ftp_port", ftp_port + ""));
        configurations.add(new Configuration("http_port", http_port + ""));
        configurations.add(new Configuration("address", address));
        configurations.add(new Configuration("basic", basic + ""));
        configurations.add(new Configuration("totp", totp + ""));
        configurations.add(new Configuration("tls", tls + ""));
        configurations.add(new Configuration("key", key));
        configurations.add(new Configuration("cert", cert));
        configurations.add(new Configuration("username", username));
        configurations.add(new Configuration("password", password));
        configurations.add(new Configuration("status", status));
        return configurations;
    }

    public static ServerConfig from(List<Configuration> configurations) {
        ServerConfig config = new ServerConfig();
        for (Configuration configuration : configurations) {
            String value = configuration.value;
            switch (configuration.key) {
                case "http":
                    config.http = Boolean.parseBoolean(value);
                    break;
                case "ftp":
                    config.ftp = Boolean.parseBoolean(value);
                    break;
                case "ftp_port":
                    config.ftp_port = Integer.parseInt(value);
                case "http_port":
                    config.http_port = Integer.parseInt(value);
                    break;
                case "address":
                    config.address = value;
                    break;
                case "basic":
                    config.basic = Boolean.parseBoolean(value);
                    break;
                case "totp":
                    config.totp = Boolean.parseBoolean(value);
                    break;
                case "tls":
                    config.tls = Boolean.parseBoolean(value);
                    break;
                case "totp_key":
                    config.key = value;
                    break;
                case "tls_cert":
                    config.cert = value;
                    break;
                case "username":
                    config.username = value;
                    break;
                case "password":
                    config.password = value;
                    break;
                case "status":
                    config.status = value;
            }
        }

        return config;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(http_port);
        dest.writeInt(ftp_port);
        dest.writeString(address);
        dest.writeByte((byte) (http ? 1 : 0));
        dest.writeByte((byte) (ftp ? 1 : 0));
        dest.writeByte((byte) (basic ? 1 : 0));
        dest.writeByte((byte) (totp ? 1 : 0));
        dest.writeByte((byte) (tls ? 1 : 0));
        dest.writeString(key);
        dest.writeString(cert);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(status);
    }
}
