package io.flutter.plugins.firebasemessaging.nexel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

import io.flutter.plugins.firebasemessaging.R;

public class CallNotificationService extends Service {
  private String CHANNEL_ID = "CallChannel";
  private String CHANNEL_NAME = "Call Channel";


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String callInfo = "";
    String name = "", callType = "", callId = "";

    int NOTIFICATION_ID = 120;

    if (intent != null && intent.getExtras() != null) {

      callInfo = intent.getExtras().getString("call_info");
      name = intent.getExtras().getString("inititator");
      callId = intent.getExtras().getString("call_id");


    }
    Intent receiveCallAction = new Intent(this, io.flutter.plugins.firebasemessaging.nexel.CallNotificationActionReceiver.class);

    receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION");
    receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
    receiveCallAction.putExtra("CALL_INFO", callInfo);
    receiveCallAction.putExtra("CALL_ID", callId);
    receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
    receiveCallAction.setAction("RECEIVE_CALL");

    Intent cancelCallAction = new Intent(this, io.flutter.plugins.firebasemessaging.nexel.CallNotificationActionReceiver.class);
    cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION");
    cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
    cancelCallAction.putExtra("CALL_INFO", callInfo);
    cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
    cancelCallAction.putExtra("CALL_ID", callId);
    cancelCallAction.setAction("CANCEL_CALL");

    Intent callDialogAction = new Intent(this, io.flutter.plugins.firebasemessaging.nexel.CallNotificationActionReceiver.class);
    callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
    callDialogAction.putExtra("CALL_INFO", callInfo);
    callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
    callDialogAction.putExtra("CALL_ID", callId);
    callDialogAction.setAction("DIALOG_CALL");

    PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(this, 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(this, 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(this, 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);

    createChannel();
    NotificationCompat.Builder notificationBuilder = null;
    Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
    notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(name)
      .setContentText("Incoming " + callType + " Call")
      .setSmallIcon(R.drawable.ic_launcher)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_CALL)
      .addAction(R.drawable.ic_launcher, "Reject", cancelCallPendingIntent)
      .addAction(R.drawable.ic_launcher, "Answer", receiveCallPendingIntent)
      .setAutoCancel(true)
      .setOngoing(true)
      .setSound(ringUri)
      .setFullScreenIntent(callDialogPendingIntent, true);


    wakeUpScreen();
    Notification incomingCallNotification = notificationBuilder.build();

    startForeground(NOTIFICATION_ID, incomingCallNotification);

    return START_STICKY;
  }


  private void wakeUpScreen() {
    PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isScreenOn();

    if (!isScreenOn) {
      PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "nexel:wake lock");
      wl.acquire(10000);
      PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "nexel: wake lock");
      wl_cpu.acquire(10000);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();// release your media player here
  }

  public void createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription("Call Notifications");
      channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
      channel.setSound(ringUri,
        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .setLegacyStreamType(AudioManager.STREAM_RING)
          .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());
      Objects.requireNonNull(this.getSystemService(NotificationManager.class)).createNotificationChannel(channel);
    }
  }
}
