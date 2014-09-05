package com.corneliudascalu.glass.phone;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * @author Corneliu Dascalu <corneliu.dascalu@osf-global.com>
 */
public class GcmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (intent.getExtras().isEmpty()) {
            sendNotification(context, "No message", "Empty error");

        } else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equalsIgnoreCase(messageType)) {
            sendNotification(context, intent.getExtras().toString(), "Error");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equalsIgnoreCase(messageType)) {
            sendNotification(context, intent.getExtras().toString(), "Deleted");
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equalsIgnoreCase(messageType)) {
            // TODO Parse message and start browser
            sendNotification(context, intent.getExtras().toString(), "Received");
        }
    }

    private void sendNotification(Context context, String msg, String title) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(2, mBuilder.build());
    }
}
