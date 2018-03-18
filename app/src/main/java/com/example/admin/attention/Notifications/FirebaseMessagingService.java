package com.example.admin.attention.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.attention.MainActivity;
import com.example.admin.attention.R;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by ADMIN on 12/18/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String TAG="FirebaseMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());




        if(remoteMessage.getData().size()>0){
            int cid;
            cid=Integer.parseInt(remoteMessage.getData().get("customid"));
            if(String.valueOf(cid).length()==8)
            {
                sendNotification(cid, remoteMessage.getNotification().getBody(), "Attention Please");
            }
        }




        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            if(MainActivity.topicsSubscribed.getInt("customid",0)!=Integer.parseInt(remoteMessage.getData().get("customid"))) {
//                MainActivity.topicsSubscribed.edit().putInt("customid", Integer.parseInt(remoteMessage.getData().get("customid"))).apply();
//                sendNotification(Integer.parseInt(remoteMessage.getData().get("customid")), remoteMessage.getNotification().getBody(), "Attention Please");
//            }
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(int id, String body, String Title) {
        //Intent intent=new Intent(this,ProfileActivity.class);
        //intent.putExtra("user_id",user_id1);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent=new Intent("MainActivity");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri notificationTone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Configure the notification channel.
//            notificationChannel.setDescription("Channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
        NotificationCompat.Builder notifiBuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Attention Please")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationTone)
                .setContentIntent(pendingIntent);



        //NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //assert notificationManager != null;
        //notificationManager.notify(0,notifiBuilder.build());

        int notification_id=(int)System.currentTimeMillis();
        NotificationManager mNotifyMgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id,notifiBuilder.build());


    }



    @Override
    public void onMessageSent(String msgId) {
        // ...
        Toast.makeText(getApplicationContext(),"Message sent Sucessfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendError(String msgId, Exception e) {
        // ...
        Toast.makeText(getApplicationContext(),"Message not sent Sucessfully",Toast.LENGTH_SHORT).show();
    }


    private void handleNow() {
    }

    private void scheduleJob() {
        
    }
}
