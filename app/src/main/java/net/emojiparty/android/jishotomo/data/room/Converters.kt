package net.emojiparty.android.jishotomo.data.room

import androidx.room.TypeConverter
import java.util.Date

object Converters {
  @JvmStatic @TypeConverter fun fromTimestamp(value: Long?): Date? {
    return value?.let { Date(it) }
  }

  @JvmStatic @TypeConverter fun dateToTimestamp(date: Date?): Long? {
    return date?.time
  }
}
