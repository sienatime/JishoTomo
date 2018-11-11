package net.emojiparty.android.jishotomo.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
    Entry.class, Sense.class, CrossReference.class, EntryFts.class, SenseFts.class
}, version = 4, exportSchema = false) public abstract class AppDatabase extends RoomDatabase {
  public abstract EntryDao entryDao();

  public abstract SenseDao senseDao();
}
