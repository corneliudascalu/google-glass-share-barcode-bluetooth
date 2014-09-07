package com.corneliudascalu.glass.phone;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.GsonBuilder;

import com.corneliudascalu.glass.device.model.Device;
import com.corneliudascalu.glass.device.model.DeviceMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.IntentUnhandledMessage;
import com.corneliudascalu.glass.phone.domain.message.backservice.UnsupportedFormatMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import de.greenrobot.event.EventBus;

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
            String message = intent.getStringExtra("Message");
            String deviceString = intent.getStringExtra("Device");
            if (message != null && deviceString != null) {
                sendNotification(context, deviceString, message);
                sendEventMessage(message, deviceString);
                openUrl(context, message);
            } else {
                EventBus.getDefault().post(new UnsupportedFormatMessage(intent.getExtras().toString()));
            }
        }
    }

    private void openUrl(Context context, String message) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        viewIntent.setData(Uri.parse(message));
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(viewIntent, 0);
        if (infos.size() > 0) {
            context.startActivity(viewIntent);
        } else {
            EventBus.getDefault()
                    .post(new IntentUnhandledMessage("Couldn't open the received URL"));
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

    private void sendEventMessage(String message, String deviceString) {
        Device device = new GsonBuilder().create().fromJson(deviceString, Device.class);
        DeviceMessage deviceMessage = DeviceMessage.create(device, message);
        EventBus.getDefault().post(deviceMessage);
    }
}
