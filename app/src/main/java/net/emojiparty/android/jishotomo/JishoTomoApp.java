package net.emojiparty.android.jishotomo;

import android.app.Application;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.di.AppComponent;
import net.emojiparty.android.jishotomo.data.di.DaggerAppComponent;
import net.emojiparty.android.jishotomo.data.di.RoomModule;

// https://blog.frankel.ch/true-singletons-with-dagger-2/
public class JishoTomoApp extends Application {
  private static AppComponent applicationComponent;
  private AnalyticsLogger analyticsLogger;

  public static AppComponent getAppComponent() {
    return applicationComponent;
  }

  @Override public void onCreate() {
    super.onCreate();
    applicationComponent = DaggerAppComponent.builder()
        .roomModule(new RoomModule(this))
        .build();
    analyticsLogger = new AnalyticsLogger(this.getApplicationContext());
    analyticsLogger.logAppOpenEvent();
  }

  public AnalyticsLogger getAnalyticsLogger() {
    return analyticsLogger;
  }
}
