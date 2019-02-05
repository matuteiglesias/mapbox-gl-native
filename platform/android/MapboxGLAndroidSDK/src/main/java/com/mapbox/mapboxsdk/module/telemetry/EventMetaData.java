package com.mapbox.mapboxsdk.module.telemetry;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.Locale;

class EventMetaData {

  private static JsonObject eventMetaData = null;


  synchronized static JsonObject getEventMetaData() {
      if (eventMetaData == null) {
        eventMetaData = new JsonObject();
        eventMetaData.addProperty("os", "android");
        eventMetaData.addProperty("manufacturer", Build.MANUFACTURER);
        eventMetaData.addProperty("brand", Build.BRAND);
        eventMetaData.addProperty("device", Build.MODEL);
        eventMetaData.addProperty("version", Build.VERSION.RELEASE);
        eventMetaData.addProperty("abi", Build.CPU_ABI);
        eventMetaData.addProperty("gpu", getGpu());
        eventMetaData.addProperty("country", Locale.getDefault().getISO3Country());
        eventMetaData.addProperty("ram", getRam());
        eventMetaData.addProperty("screenSize", getWindowSize());
      }
      return eventMetaData;
  }

  private static String getRam() {
    ActivityManager actManager =
            (ActivityManager) Mapbox.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
    actManager.getMemoryInfo(memInfo);
    return String.valueOf(memInfo.totalMem);
  }

  private static String getWindowSize() {
    WindowManager windowManager =
            (WindowManager) Mapbox.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);
    int width = metrics.widthPixels;
    int height = metrics.heightPixels;

    return "{" + width + "," + height + "}";
  }

  private static String getGpu() {
//    String vendor = GLES20.glGetString(GLES20.GL_VENDOR);
//    GLES20.glGetString(GLES20.GL_VERSION);

//    Runtime.getRuntime().exec(commandToRun);
//    Grep.e(UiDevice.getInstance(getInstrumentation())
//            .executeShellCommand("dumpsys")!!, "GLES")
//                .removePrefix("GLES: ");
    return "";
  }

}
