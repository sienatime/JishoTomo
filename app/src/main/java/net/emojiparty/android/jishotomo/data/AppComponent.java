package net.emojiparty.android.jishotomo.data;

import android.app.Application;
import dagger.Component;
import javax.inject.Singleton;
import net.emojiparty.android.jishotomo.ui.DefinitionActivity;
import net.emojiparty.android.jishotomo.ui.DrawerActivity;

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb
@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class})
public interface AppComponent {
  void inject(DrawerActivity drawerActivity);
  void inject(DefinitionActivity definitionActivity);
  EntryDao entryDao();
  AppDatabase db();
  Application application();
}
