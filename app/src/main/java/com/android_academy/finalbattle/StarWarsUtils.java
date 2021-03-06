package com.android_academy.finalbattle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;

public class StarWarsUtils {

  //public static final String STAR_WARS_LOCATION = "http://starwars.com";
  public static final String STAR_WARS_LOCATION = "https://google.com";
  public static final String SHOW_RESULT_ACTION = "SHOW_RESULT";

  public static void addRetryTask(LukeDecision decision, Context context) {

    Bundle bundle = new Bundle();
    bundle.putSerializable(LukeDecision.class.getSimpleName(), decision);

    GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);
    Task task = new OneoffTask.Builder().setService(BestTimeService.class)
        .setExecutionWindow(0, 30)
        .setTag(BestTimeService.LUKE_DECISION)
        .setUpdateCurrent(false)
        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
        .setRequiresCharging(false)
        .setExtras(bundle)
        .build();

    gcmNetworkManager.schedule(task);
  }

  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(StarWarsUtils.class.getSimpleName(), Context.MODE_PRIVATE);
  }

  public static LukeDecision getDecisionToRetry(Context context) {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    String decisionStr = sharedPreferences.getString(LukeDecision.class.getSimpleName(), "");
    sharedPreferences.edit().clear().apply();
    return LukeDecision.valueOf(decisionStr);
  }

  public enum LukeDecision {
    LUKE_KILL_DARTH_VADER,
    STAY_ON_LIGHT_SIDE
  }

  public static boolean isNetworkActive(Context context) {
    ConnectivityManager connManager = provideConnectivityManager(context);
    NetworkInfo netInfo = connManager.getActiveNetworkInfo();

    return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
  }

  public static ConnectivityManager provideConnectivityManager(Context context) {
    return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public static void makeDecision(LukeDecision decision, Context context) {
    // Immediately make network call.
    Intent nowIntent = new Intent(context, NowIntentService.class);
    nowIntent.putExtra(LukeDecision.class.getSimpleName(), decision);
    context.startService(nowIntent);
  }
}
