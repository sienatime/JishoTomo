package net.emojiparty.android.jishotomo.data.di;

import android.app.Application;
import dagger.Component;
import javax.inject.Singleton;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.room.AppDatabase;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity;

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb
@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class})
public interface AppComponent {
  void inject(DrawerActivity drawerActivity);
  void inject(AppRepository appRepository);
  EntryDao entryDao();
  SenseDao senseDao();
  AppDatabase db();
  Application application();
}