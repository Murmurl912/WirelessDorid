package com.example.httpserver.app.ui.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.example.httpserver.R;
import com.example.httpserver.app.MainActivity;

public class ServerNotification {

    private static NotificationCompat.Builder buildNotification(
            Context context, String title, String contentText, boolean noStopButton) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        long when = System.currentTimeMillis();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationConstants.CHANNEL_SERVER_ID)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_server_icon)
                        .setTicker(context.getText(R.string.server_is_running))
                        .setWhen(when)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true);

        if (!noStopButton) {
            int stopIcon = android.R.drawable.ic_menu_close_clear_cancel;
            Intent intent = new Intent(ServiceReceiver.ACTION_STOP_SERVER).setPackage(context.getPackageName());
            PendingIntent stopPendingIntent =
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.addAction(stopIcon, context.getString(R.string.stop_server), stopPendingIntent);
        }

        NotificationConstants.setMetadata(context, builder, NotificationConstants.TYPE_SERVER);

        return builder;
    }

    public static Notification startNotification(Context context, boolean noStopButton) {
        NotificationCompat.Builder builder =
                buildNotification(
                        context, context.getString(R.string.server_is_running),
                        "",
                        noStopButton);

        return builder.build();
    }

    public static void updateNotification(Context context, boolean noStopButton) {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(notificationService);


        NotificationCompat.Builder builder =
                buildNotification(
                        context,
                        context.getString(R.string.server_is_running),
                        "",
                        noStopButton);

        notificationManager.notify(NotificationConstants.SERVER_ID, builder.build());
    }

    private static void removeNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
        nm.cancelAll();
    }
}
