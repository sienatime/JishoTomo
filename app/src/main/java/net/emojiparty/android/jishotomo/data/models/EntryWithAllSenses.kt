package net.emojiparty.android.jishotomo.data.models

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.splitAndJoin

data class EntryWithAllSenses(
  @Embedded var entry: Entry,
  @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Sense::class)
  var senses: List<SenseWithCrossReferences> = emptyList()
) {

  val kanjiOrReading: String
    get() = if (hasKanji()) entry.primaryKanji!! else entry.primaryReading

  val reading: String?
    get() = if (hasKanji()) entry.primaryReading else null

  fun hasKanji(): Boolean {
    return entry.primaryKanji != null
  }

  @Ignore
  val alternateKanji: String = entry.otherKanji?.splitAndJoin() ?: ""

  @Ignore
  val alternateReadings: String = entry.otherReadings?.splitAndJoin() ?: ""

  fun hasAlternateKanji(): Boolean {
    return entry.otherKanji != null
  }

  fun hasAlternateReadings(): Boolean {
    return entry.otherReadings != null
  }
}
