package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Entry.class, Sense.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
  public abstract EntryDao entryDao();
  public abstract SenseDao senseDao();
}
