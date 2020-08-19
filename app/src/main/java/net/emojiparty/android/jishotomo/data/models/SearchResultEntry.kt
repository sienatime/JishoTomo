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
  override var id: Int,
  @ColumnInfo(name = "primary_kanji") override var primaryKanji: String?,
  @ColumnInfo(name = "primary_reading") override var primaryReading: String
) : BasicEntry() {

  @Relation(
    parentColumn = "id",
    entityColumn = "entry_id",
    entity = Sense::class,
    projection = ["glosses"]
  )
  var glosses: List<String> = emptyList()

  fun getPrimaryGloss(): String = glosses[0].splitAndJoin()
}
