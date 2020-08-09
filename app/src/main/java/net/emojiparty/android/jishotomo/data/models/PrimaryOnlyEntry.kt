package net.emojiparty.android.jishotomo.data.models

import androidx.room.ColumnInfo

// TODO revisit inheritance of this class (SearchResultEntry and CrossReferencedEntry)
data class PrimaryOnlyEntry(
  var id: Int,
  @ColumnInfo(name = "primary_kanji") var primaryKanji: String? = null,
  @ColumnInfo(name = "primary_reading") var primaryReading: String
) {
  fun hasKanji(): Boolean = primaryKanji != null

  fun kanjiOrReading(): String? = if (hasKanji()) primaryKanji else primaryReading

  fun reading(): String? = if (hasKanji()) primaryReading else null

}
