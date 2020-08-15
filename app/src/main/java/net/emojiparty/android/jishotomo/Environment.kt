package net.emojiparty.android.jishotomo

import android.util.Log

object Environment {
  val isTest: Boolean by lazy {
    try {
      Class.forName("androidx.test.espresso.Espresso")
      Log.i("Environment", "isTest true")
      true
    } catch (e: ClassNotFoundException) {
      Log.i("Environment", "isTest false")
      false
    }
  }
}
