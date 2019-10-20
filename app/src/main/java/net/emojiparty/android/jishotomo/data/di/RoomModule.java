package net.emojiparty.android.jishotomo.data.di;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
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
    final String databaseName = "jishotomo.db";
    db = Room.databaseBuilder(application, AppDatabase.class, databaseName).createFromAsset("databases/" + databaseName)
        .addMigrations(new Migration(1, 2) {
          @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE entries ADD COLUMN favorited INTEGER");
          }
        })
        .addMigrations(new Migration(2, 3) {
          @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `entriesFts` USING FTS4(`primary_kanji`, `primary_reading`, `other_kanji`, `other_readings`, content=`entries`)");
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `sensesFts` USING FTS4(`glosses`, content=`senses`)");
            database.execSQL("INSERT INTO entriesFts(entriesFts) VALUES ('rebuild')");
            database.execSQL("INSERT INTO sensesFts(sensesFts) VALUES ('rebuild')");
          }
        })
        .build();
  }

  // this is needed in order to provide the DAOs
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
