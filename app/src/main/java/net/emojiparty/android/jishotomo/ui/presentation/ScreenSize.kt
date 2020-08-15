package net.emojiparty.android.jishotomo.ui.presentation

import android.content.res.Resources
import android.util.TypedValue

// http://android.pcsalt.com/set-margins-in-dp-programmatically-android/
object ScreenSize {
  fun dpsToPixels(
    dps: Float,
    resources: Resources
  ): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dps,
        resources.displayMetrics
    ).toInt()
  }
}