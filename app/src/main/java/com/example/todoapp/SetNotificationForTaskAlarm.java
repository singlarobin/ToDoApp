package com.example.todoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.example.todoapp.data.WorkListContract;


public class SetNotificationForTaskAlarm extends BroadcastReceiver {
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    String groupNotificationKey="com.example.todoapp";
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title=intent.getExtras().getString(WorkListContract.WorkListEntry.notificationDescription);
        createNotificationChannel(context);
        deliverNotification(context,title);

   }

    private void deliverNotification(Context context,String title) {
        Intent notificationIntent = new Intent(context, SetNotificationForTaskAlarm.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle("ToDo's")
                .setContentText("Complete Your "+title +" Task.")
                .setGroup(groupNotificationKey)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }

    public void createNotificationChannel(Context context){
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "ToDo's  Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorAccent);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from ToDo's");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }


}