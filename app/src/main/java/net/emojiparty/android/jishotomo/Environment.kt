package net.emojiparty.android.jishotomo

import android.util.Log

class Environment {
  companion object {
    @JvmStatic
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
}
