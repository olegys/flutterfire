package io.flutter.plugins.firebasemessaging.nexel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import io.flutter.plugins.firebasemessaging.retrofit.ApiManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
      String callId = "";
      callId = intent.getStringExtra("CALL_ID");


      if (action != null && !action.equalsIgnoreCase("")) {
        performClickAction(context, action, callInfo, callId);
      }

      // Close the notification after the click action is performed.
      Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
      context.sendBroadcast(iclose);
      context.stopService(new Intent(context, io.flutter.plugins.firebasemessaging.nexel.CallNotificationService.class));

    }


  }

  private void performClickAction(Context context, String action, String callInfo, String callId) {

    if(action.equalsIgnoreCase("CANCEL_CALL")){

      Log.e("########", "Receive CANCEL_CALL: with call id " +callId);



      SharedPreferences sharedPref = context.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE);
      String token = sharedPref.getString("flutter.USER_AUTH_TOKEN", "");



      ApiManager.getInstance().getCallForId("Bearer "+token,callId).enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
      });

    }

    if (action.equalsIgnoreCase("RECEIVE_CALL")) {

      if (checkAppPermissions()) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.setAction(Intent.ACTION_RUN);
//        intent.putExtra("route", "/splash?" + callInfo+"&answered=true");
        intent.putExtra("call_info",  callInfo+"&answered=true");
        context.startActivity(intent);
//        intentCallReceive.putExtra("Call", "incoming");
//        intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      } else {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.setAction(Intent.ACTION_RUN);
//        intent.putExtra("route", "/splash?" + callInfo+"&answered=true");
        intent.putExtra("call_info",  callInfo+"&answered=true");

        context.startActivity(intent);

      }
    } else if (action.equalsIgnoreCase("DIALOG_CALL")) {

      // show ringing activity when phone is locked
      Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
      intent.setAction(Intent.ACTION_RUN);
//      intent.putExtra("route", "/splash?" + callInfo+"&answered=false");
      intent.putExtra("call_info",  callInfo+"&answered=false");
      context.startActivity(intent);



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
