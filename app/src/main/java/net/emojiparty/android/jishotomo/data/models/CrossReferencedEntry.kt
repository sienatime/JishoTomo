package net.emojiparty.android.jishotomo.data.models

import androidx.room.ColumnInfo

data class CrossReferencedEntry(
  override var id: Int,
  @ColumnInfo(name = "primary_kanji") override var primaryKanji: String?,
  @ColumnInfo(name = "primary_reading") override var primaryReading: String,
  var senseId: Int
): BasicEntry()
