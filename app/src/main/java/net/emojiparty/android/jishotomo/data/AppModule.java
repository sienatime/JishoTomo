package net.emojiparty.android.jishotomo.data;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb
@Module
public class AppModule {
  Application application;

  public AppModule(Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Application providesApplication() {
    return application;
  }
}
