package net.emojiparty.android.jishotomo.data.di;

import android.app.Application;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;
import com.huma.room_for_asset.RoomAsset;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import net.emojiparty.android.jishotomo.data.room.AppDatabase;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb

@Module public class RoomModule {
  private AppDatabase db;

  @Singleton public RoomModule(Application application) {
    db = RoomAsset.databaseBuilder(application, AppDatabase.class, "jishotomo.db")
        .addMigrations(new Migration(2, 3) {
          @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE entries ADD COLUMN favorited INTEGER DEFAULT 0");
          }
        })
        .build();
  }

  @Singleton @Provides AppDatabase providesRoomDatabase() {
    return db;
  }

  @Singleton @Provides EntryDao providesEntryDao(AppDatabase db) {
    return db.entryDao();
  }

  @Singleton @Provides SenseDao providesSenseDao(AppDatabase db) {
    return db.senseDao();
  }
}
