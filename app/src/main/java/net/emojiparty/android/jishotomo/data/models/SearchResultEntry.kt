package net.emojiparty.android.jishotomo.data.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.splitAndJoin

// this should have only the things needed to display the search result:
//   the Entry's primary_kanji, primary_reading
//   the first Sense
//     that Sense's glosses
data class SearchResultEntry(
  var id: Int,
  @ColumnInfo(name = "primary_kanji") var primaryKanji: String?,
  @ColumnInfo(name = "primary_reading") var primaryReading: String
) {
  @Relation(
      parentColumn = "id",
      entityColumn = "entry_id",
      entity = Sense::class,
      projection = ["glosses"]
  )
  var glosses: List<String> = emptyList()

  fun getPrimaryGloss(): String = glosses[0].splitAndJoin()

  fun hasKanji(): Boolean = primaryKanji != null

  fun kanjiOrReading(): String = if (hasKanji()) primaryKanji!! else primaryReading

  fun reading(): String? = if (hasKanji()) primaryReading else null
}
