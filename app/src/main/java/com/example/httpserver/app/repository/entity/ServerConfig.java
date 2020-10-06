package com.example.httpserver.app.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig implements Parcelable {
    public int port = 8080;
    public String address = "all";
    public boolean basic = false;
    public boolean totp = true;
    public boolean tls = true;
    public String key = "default";
    public String cert = "default";
    public String username = "";
    public String password = "";

    public static final String[] keys = {
            "port",
            "address",
            "basic",
            "totp",
            "tls",
            "totp_key",
            "tls_cert",
            "username",
            "password"
    };

    public ServerConfig() {

    }

    protected ServerConfig(Parcel in) {
        port = in.readInt();
        address = in.readString();
        basic = in.readByte() != 0;
        totp = in.readByte() != 0;
        tls = in.readByte() != 0;
        key = in.readString();
        cert = in.readString();
        username = in.readString();
        password = in.readString();
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
        configurations.add(new Configuration("port", port + ""));
        configurations.add(new Configuration("address", address));
        configurations.add(new Configuration("basic", basic + ""));
        configurations.add(new Configuration("totp", totp + ""));
        configurations.add(new Configuration("tls", tls + ""));
        configurations.add(new Configuration("key", key));
        configurations.add(new Configuration("cert", cert));
        configurations.add(new Configuration("username", username));
        configurations.add(new Configuration("password", password));
        return configurations;
    }

    public static ServerConfig from(List<Configuration> configurations) {
        ServerConfig config = new ServerConfig();
        for (Configuration configuration : configurations) {
            String value = configuration.value;
            switch (configuration.key) {
                case "port":
                    config.port = Integer.parseInt(value);
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
        dest.writeInt(port);
        dest.writeString(address);
        dest.writeByte((byte) (basic ? 1 : 0));
        dest.writeByte((byte) (totp ? 1 : 0));
        dest.writeByte((byte) (tls ? 1 : 0));
        dest.writeString(key);
        dest.writeString(cert);
        dest.writeString(username);
        dest.writeString(password);
    }
}
