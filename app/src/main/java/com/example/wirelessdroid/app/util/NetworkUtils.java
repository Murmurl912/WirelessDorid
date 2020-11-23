package com.example.wirelessdroid.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkUtils {
    private static final String WIFI_AP_ADDRESS_PREFIX = "192.168.43.";

    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isEnabledWifiHotspot(Context context) {
        WifiManager wm =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Boolean enabled = callIsWifiApEnabled(wm);
        return enabled != null ? enabled : false;
    }


    public static boolean isConnectedToLocalNetwork(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        boolean connected =
                ni != null
                        && ni.isConnected()
                        && (ni.getType() & (ConnectivityManager.TYPE_WIFI | ConnectivityManager.TYPE_ETHERNET))
                        != 0;
        if (!connected) {
            try {
                for (NetworkInterface netInterface :
                        Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (netInterface.getDisplayName().startsWith("rndis")) {
                        connected = true;
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    public static InetAddress intToInet(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = byteOfInt(value, i);
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            // This only happens if the byte array has a bad length
            return null;
        }
    }

    public static byte byteOfInt(int value, int which) {
        int shift = which * 8;
        return (byte) (value >> shift);
    }

    public static InetAddress getLocalInetAddress(Context context) {
        if (!isConnectedToLocalNetwork(context) && !isEnabledWifiHotspot(context)) {
            return null;
        }

        if (isConnectedToWifi(context)) {
            WifiManager wm =
                    (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ipAddress = wm.getConnectionInfo().getIpAddress();
            if (ipAddress == 0) return null;
            return intToInet(ipAddress);
        }

        try {
            Enumeration<NetworkInterface> netinterfaces = NetworkInterface.getNetworkInterfaces();
            while (netinterfaces.hasMoreElements()) {
                NetworkInterface netinterface = netinterfaces.nextElement();
                Enumeration<InetAddress> addresses = netinterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address == null) {
                        continue;
                    }

                    if (address.getHostAddress().startsWith(WIFI_AP_ADDRESS_PREFIX)
                            && isEnabledWifiHotspot(context)) return address;

                    // this is the condition that sometimes gives problems
                    if (!address.isLoopbackAddress()
                            && !address.isLinkLocalAddress()
                            && !isEnabledWifiHotspot(context)) return address;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static Boolean callIsWifiApEnabled(@NonNull WifiManager wifiManager) {
        Boolean r = null;
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            r = (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return r;
    }
}
