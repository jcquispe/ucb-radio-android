package com.muvlin.ucbradio.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.util.Util;
import com.muvlin.ucbradio.MainActivity;
import com.muvlin.ucbradio.R;

public class MediaNotificationManager {
    public static final int NOTIFICATION_ID = 555;
    private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
    private final String PRIMARY_CHANNEL_NAME = "PRIMARY";

    private RadioService service;

    private String text, strAppName, strLiveBroadcast;

    private Resources resources;

    private NotificationManagerCompat notificationManager;

    public MediaNotificationManager(RadioService service) {

        this.service = service;
        this.resources = service.getResources();

        text = resources.getString(R.string.text);
        strAppName = resources.getString(R.string.app_name);
        strLiveBroadcast = resources.getString(R.string.live_broadcast);

        notificationManager = NotificationManagerCompat.from(service);
    }

    public void startNotify(String playbackStatus) {
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);

        int icon = R.drawable.ic_pause_white;
        Intent playbackAction = new Intent(service, RadioService.class);
        playbackAction.setAction(RadioService.ACTION_PAUSE);

        int pendingFlags;
        if (Util.SDK_INT >= 23) {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, pendingFlags);

        if(playbackStatus.equals(PlaybackStatus.PAUSED)){
            icon = R.drawable.ic_play_white;
            playbackAction.setAction(RadioService.ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, pendingFlags);

        }

        Intent stopIntent = new Intent(service, RadioService.class);
        stopIntent.setAction(RadioService.ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, pendingFlags);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, pendingFlags);

        notificationManager.cancel(NOTIFICATION_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, PRIMARY_CHANNEL)
                .setColorized(false)
                .setColor(Color.parseColor("#1d315d"))
                .setAutoCancel(false)
                .setContentTitle(strLiveBroadcast)
                .setContentText(text)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.ic_stop_white, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction));

        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotify() {

        service.stopForeground(true);
    }
}
