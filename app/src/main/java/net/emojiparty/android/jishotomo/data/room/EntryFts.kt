package net.emojiparty.android.jishotomo.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Entry::class)
@Entity(tableName = "entriesFts")
data class EntryFts(
  @ColumnInfo(name = "primary_kanji") val primaryKanji: String,
  @ColumnInfo(name = "primary_reading") val primaryReading: String,
  @ColumnInfo(name = "other_kanji") val otherKanji: String,
  @ColumnInfo(name = "other_readings") val otherReadings: String
)
