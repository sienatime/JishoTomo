package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Entry.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
  public abstract EntryDao entryDao();
}
