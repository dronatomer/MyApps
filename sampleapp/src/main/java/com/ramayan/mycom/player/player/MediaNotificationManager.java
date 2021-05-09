package com.ramayan.mycom.player.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ramayan.mycom.Home.MainActivity;
import com.ramayan.mycom.Model.MediaMetaData;
import com.ramayan.mycom.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MediaNotificationManager {

    public static final int NOTIFICATION_ID = 555;
    private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
    private final String PRIMARY_CHANNEL_NAME = "PRIMARY";

    private RadioService service;

    private String strAppName, strLiveBroadcast;

    private Resources resources;

    private NotificationManagerCompat notificationManager;
    private Bitmap largeIcon = null;
    private String playbackStatus = null;
    private Bitmap myBitmap = null;

    public MediaNotificationManager(RadioService service) {

        this.service = service;
        this.resources = service.getResources();

        strAppName = resources.getString(R.string.app_name);
        strLiveBroadcast = resources.getString(R.string.live_broadcast);

        notificationManager = NotificationManagerCompat.from(service);

    }


    public class MyAsync extends AsyncTask<MediaMetaData, Void, MediaMetaData> {

        @Override
        protected MediaMetaData doInBackground(MediaMetaData... params) {

            try {
                URL url = new URL(params[0].getmediaImage());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                largeIcon = BitmapFactory.decodeStream(input);
                return params[0];
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MediaMetaData currentObject) {
            super.onPostExecute(currentObject);
            if (largeIcon == null) {
                largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
            }
            startNotifyAfter(currentObject);
        }
    }

    public void startNotifyAfter(MediaMetaData currentObject){
        try {

            if(currentObject!=null)

            strLiveBroadcast = currentObject.getMediaTitle();
            int icon = R.drawable.play_icon_24;
            Intent playbackAction = new Intent(service, RadioService.class);
            playbackAction.setAction(RadioService.ACTION_PAUSE);
            PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0);

            if (playbackStatus.equals(PlaybackStatus.PAUSED)) {

                icon = R.drawable.pause_icon_24;
                playbackAction.setAction(RadioService.ACTION_PLAY);
                action = PendingIntent.getService(service, 2, playbackAction, 0);

            }


            Intent stopIntent = new Intent(service, RadioService.class);
            stopIntent.setAction(RadioService.ACTION_STOP);
            PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);

            Intent nextIntent = new Intent(service, RadioService.class);
            nextIntent.setAction(RadioService.ACTION_NEXT);
            PendingIntent nextAction = PendingIntent.getService(service, 4, nextIntent, 0);

            Intent previousIntent = new Intent(service, RadioService.class);
            previousIntent.setAction(RadioService.ACTION_PREVIOUS);
            PendingIntent previousAction = PendingIntent.getService(service, 5, previousIntent, 0);

            Intent intent = new Intent(service, MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra("From", "notifyFrag");
            PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);


            notificationManager.cancel(NOTIFICATION_ID);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(service, PRIMARY_CHANNEL)
                    .setAutoCancel(false)
                    .setContentTitle(strLiveBroadcast)
                    .setContentText(strAppName)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(android.R.drawable.stat_sys_headset)
                    .addAction(R.drawable.icon_previous_32,"previous", previousAction)
                    .addAction(icon, "pause", action)
                    .addAction(R.drawable.icon_next_32,"farward", nextAction)
                    .addAction(R.drawable.ic_stop_white, "stop", stopAction)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(service.getMediaSession().getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2, 3)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(stopAction));

            service.startForeground(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startNotify(String playbackStatus, MediaMetaData currentObject) {
        MyAsync myAsync = new MyAsync();
        myAsync.execute(currentObject);
        this.playbackStatus = playbackStatus;
    }


    public void cancelNotify() {
        service.stopForeground(true);
    }

}
