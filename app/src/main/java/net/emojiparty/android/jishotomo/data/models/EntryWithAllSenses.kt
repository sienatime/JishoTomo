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
  var senses: List<Sense> = emptyList()
) : BasicEntry() {

  @Ignore override var primaryKanji = entry.primaryKanji
  @Ignore override var primaryReading = entry.primaryReading
  @Ignore override var id = entry.id
  @Ignore val otherKanji = entry.otherKanji
  @Ignore val otherReadings = entry.otherReadings
  @Ignore val jlptLevel = entry.jlptLevel

  val isFavorited
    get() = entry.isFavorited()

  @Ignore
  val alternateKanji: String = otherKanji?.splitAndJoin() ?: ""

  @Ignore
  val alternateReadings: String = otherReadings?.splitAndJoin() ?: ""

  fun hasAlternateKanji(): Boolean {
    return otherKanji != null
  }

  fun hasAlternateReadings(): Boolean {
    return otherReadings != null
  }
}
