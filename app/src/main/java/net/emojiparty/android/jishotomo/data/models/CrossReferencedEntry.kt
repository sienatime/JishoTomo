package net.emojiparty.android.jishotomo.data.models

import androidx.room.ColumnInfo

data class CrossReferencedEntry(
  var id: Int,
  @ColumnInfo(name = "primary_kanji") var primaryKanji: String?,
  @ColumnInfo(name = "primary_reading") var primaryReading: String,
  var senseId: Int
) {
  fun hasKanji(): Boolean = primaryKanji != null

  fun kanjiOrReading(): String? = if (hasKanji()) primaryKanji else primaryReading

  fun reading(): String? = if (hasKanji()) primaryReading else null
}
