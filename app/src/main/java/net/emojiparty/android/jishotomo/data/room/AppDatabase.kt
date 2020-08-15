package net.emojiparty.android.jishotomo.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [Entry::class, Sense::class, CrossReference::class, EntryFts::class, SenseFts::class],
  version = 3, exportSchema = false
)
@TypeConverters(
  Converters::class
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun entryDao(): EntryDao
  abstract fun senseDao(): SenseDao
}
