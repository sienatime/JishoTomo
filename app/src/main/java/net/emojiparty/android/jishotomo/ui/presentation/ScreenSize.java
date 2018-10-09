package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.res.Resources;
import android.util.TypedValue;

// http://android.pcsalt.com/set-margins-in-dp-programmatically-android/
public class ScreenSize {
  public static int dpsToPixels(float dps, Resources resources) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
        resources.getDisplayMetrics());
  }
}
