package com.mezyapps.bni_visitor.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.MainActivity;
import com.mezyapps.bni_visitor.activity.NotificationActivity;

import java.util.Random;

public class Alarm  extends BroadcastReceiver {

    public static final String CHANNEL_ID = "mychannelid";
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    int m;
    String title="Follow Up",visitor_id,visitor_name;
    String description="New Follow";

    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer=MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.start();

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            visitor_id = bundle.getString("id");
            visitor_name = bundle.getString("Name");
        }


        final String NOTIFICATION_CHANNEL_ID = "10001";
        Random random = new Random();
        m = random.nextInt(9999 - 1000) + 1000;
        long[] v = {500, 1000};

        Intent intentFlag;

        intentFlag = new Intent(context, NotificationActivity.class);
        intentFlag.putExtra("ID", visitor_id);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intentFlag,PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(description+" "+visitor_name)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setSound(uri)
                .setColor(ContextCompat.getColor(context, R.color.app_color))
                .setVibrate(v)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent);


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        adminChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        builder.setChannelId(CHANNEL_ID);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }

        if (notificationManager != null) {
            notificationManager.notify(m, builder.build());
        }
    }
}
