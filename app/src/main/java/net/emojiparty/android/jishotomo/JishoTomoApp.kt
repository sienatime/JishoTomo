package net.emojiparty.android.jishotomo

import android.app.Application
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.di.AppComponent
import net.emojiparty.android.jishotomo.data.di.DaggerAppComponent
import net.emojiparty.android.jishotomo.data.di.RoomModule

// https://blog.frankel.ch/true-singletons-with-dagger-2/
class JishoTomoApp : Application() {
  lateinit var analyticsLogger: AnalyticsLogger
    private set

  override fun onCreate() {
    super.onCreate()
    appComponent = DaggerAppComponent.builder()
      .roomModule(RoomModule(this))
      .build()
    analyticsLogger = AnalyticsLogger()
    analyticsLogger.logAppOpenEvent()
  }

  companion object {
    var appComponent: AppComponent? = null
      private set
  }
}
