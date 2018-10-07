package net.emojiparty.android.jishotomo.ui.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

// http://android.pcsalt.com/set-margins-in-dp-programmatically-android/
public class ScreenSize {
  public static int dpsToPixels(float dps, Resources resources) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
        resources.getDisplayMetrics());
  }

  public static boolean isWideLayout(Activity activity) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    int width = metrics.heightPixels;
    return width >= dpsToPixels(600, activity.getResources());
  }
}
