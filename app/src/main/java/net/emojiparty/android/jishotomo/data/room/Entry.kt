package net.emojiparty.android.jishotomo.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * An Entry has many Senses, which can be thought of like a definition.
 *
 * Ex. an entry with the kanji 行けない and reading いけない has several meanings:
 * 1. wrong, not good, of no use
 * 2. hopeless, past hope
 * 3. must not do
 */
@Entity(tableName = "entries", indices = [Index("jlpt_level")])
data class Entry(
  @PrimaryKey val id: Int,
  @ColumnInfo(name = "primary_kanji") val primaryKanji: String? = null,
  @ColumnInfo(name = "primary_reading") val primaryReading: String,
  @ColumnInfo(name = "other_kanji") val otherKanji: String? = null,
  @ColumnInfo(name = "other_readings") val otherReadings: String? = null,
  @ColumnInfo(name = "jlpt_level") val jlptLevel: Int? = null,
  var favorited: Date? = null
) {
  fun isFavorited(): Boolean {
    return favorited != null
  }

  fun toggleFavorited() {
    favorited = if (isFavorited()) {
      null
    } else {
      Date()
    }
  }

  fun hasKanji(): Boolean = primaryKanji != null

  fun kanjiOrReading(): String = if (hasKanji()) primaryKanji!! else primaryReading

  fun reading(): String? = if (hasKanji()) primaryReading else null
}
