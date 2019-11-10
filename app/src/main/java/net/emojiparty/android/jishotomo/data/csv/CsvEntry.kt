package net.emojiparty.android.jishotomo.data.csv

import net.emojiparty.android.jishotomo.data.SemicolonSplit
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay

class CsvEntry(private val entry: EntryWithAllSenses, private val senseDisplay: SenseDisplay) {
  fun toArray(): Array<String> {
    return arrayOf(entry.getKanjiOrReading(), meaning(entry), reading(entry))
  }

  private fun meaning(entry: EntryWithAllSenses): String {
    val builder = StringBuilder()
    val numberOfSenses = entry.getSenses().size
    var glossIndex = 1;
    for (i in 0 until numberOfSenses) {
      val sense = entry.getSenses()[i]

      val newPartOfSpeech = appendPartsOfSpeech(builder, sense.getSense())
      if (newPartOfSpeech) {
        glossIndex = 1
      }
      if (numberOfSenses > 1) {
        builder.append(glossIndex)
        builder.append(". ")
        glossIndex++
      }
      builder.append(SemicolonSplit.splitAndJoin(sense.getSense().glosses))
      builder.append("<br/>")
    }
    return builder.toString()
  }

  private fun appendPartsOfSpeech(builder: StringBuilder, sense: Sense): Boolean {
    if (sense.partsOfSpeech != null) {
      builder.append(senseDisplay.formatPartsOfSpeech(sense.partsOfSpeech))
      builder.append("<br/>")
      return true
    }
    return false
  }

  private fun reading(entry: EntryWithAllSenses): String {
    return if (entry.hasKanji()) {
      String.format(
          "%s[%s]", entry.entry.primaryKanji,
          entry.entry.primaryReading
      )
    } else {
      entry.entry.primaryReading
    }
  }
}
