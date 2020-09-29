package io.flutter.plugins.firebasemessaging.nexel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import io.flutter.embedding.android.FlutterActivity;


public class CallNotificationActionReceiver extends BroadcastReceiver {


  Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    this.mContext = context;
    if (intent != null && intent.getExtras() != null) {

      String action = "";
      action = intent.getStringExtra("ACTION_TYPE");
      String callInfo = "";
      callInfo = intent.getStringExtra("CALL_INFO");


      if (action != null && !action.equalsIgnoreCase("")) {
        performClickAction(context, action, callInfo);
      }

      // Close the notification after the click action is performed.
      Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
      context.sendBroadcast(iclose);
      context.stopService(new Intent(context, io.flutter.plugins.firebasemessaging.nexel.CallNotificationService.class));

    }


  }


//                   .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                   .setFlags(Intent.FLAG_FROM_BACKGROUND)


//    FlutterActivity
//            .withNewEngine()
//            .initialRoute("/home").build(context)

  private void performClickAction(Context context, String action, String callInfo) {
    if (action.equalsIgnoreCase("RECEIVE_CALL")) {

      if (checkAppPermissions()) {
        Intent intentCallReceive = FlutterActivity
          .withNewEngine()
          .initialRoute("/splash?" + callInfo +"&answered=true").build(context).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentCallReceive.putExtra("Call", "incoming");
        intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intentCallReceive);
      } else {
        Intent intent = FlutterActivity
          .withNewEngine()
          .initialRoute("/splash?" + callInfo+"&answered=true").build(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CallFrom", "call from push");
        mContext.startActivity(intent);

      }
    } else if (action.equalsIgnoreCase("DIALOG_CALL")) {

      // show ringing activity when phone is locked
      Intent intent = FlutterActivity
        .withNewEngine()
        .initialRoute("/splash?" + callInfo+"&answered=false").build(context).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      mContext.startActivity(intent);


    } else {
      context.stopService(new Intent(context, io.flutter.plugins.firebasemessaging.nexel.CallNotificationService.class));
      Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
      context.sendBroadcast(it);
    }
  }

  private Boolean checkAppPermissions() {
    return hasReadPermissions() && hasWritePermissions() && hasAudioPermissions();
  }

  private boolean hasAudioPermissions() {
    return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
  }

  private boolean hasReadPermissions() {
    return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
  }

  private boolean hasWritePermissions() {
    return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
  }

  private boolean hasCameraPermissions() {
    return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
  }
}
