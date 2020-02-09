package net.emojiparty.android.jishotomo.data.csv

import androidx.annotation.VisibleForTesting
import net.emojiparty.android.jishotomo.data.CJKUtil
import net.emojiparty.android.jishotomo.data.SemicolonSplit
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay

class CsvEntry(private val entry: EntryWithAllSenses, private val senseDisplay: SenseDisplay) {
  fun toArray(): Array<String> {
    return arrayOf(entry.kanjiOrReading, meaning(), reading())
  }

  @VisibleForTesting
  fun meaning(): String {
    val builder = StringBuilder()
    val numberOfSenses = entry.senses.size
    var glossIndex = 1;
    for (i in 0 until numberOfSenses) {
      val sense = entry.senses[i]

      val newPartOfSpeech = appendPartsOfSpeech(builder, sense.sense)
      if (newPartOfSpeech) {
        glossIndex = 1
      }
      if (numberOfSenses > 1) {
        builder.append(glossIndex)
        builder.append(". ")
        glossIndex++
      }
      builder.append(SemicolonSplit.splitAndJoin(sense.sense.glosses))
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

  @VisibleForTesting
  fun reading(): String {
    return if (entry.hasKanji()) {
      formatReading(entry.entry)
    } else {
      entry.entry.primaryReading
    }
  }

  private fun formatReading(entry: Entry): String {
    val kanji = entry.primaryKanji
    val reading = entry.primaryReading

    val builder = StringBuilder()

    val kanjiStartIndices = mutableListOf<Int>(0)
    val readingStartIndices = mutableListOf(0)

    var lastWasKana = false

    for (i in kanji.indices) {
      val codePoint = Character.codePointAt(kanji, i)
      if (CJKUtil.isKana(codePoint)) {
        lastWasKana = true
      } else {
        if (lastWasKana) {
          kanjiStartIndices.add(i)
        }
        lastWasKana = false
      }
    }

    val kanjiTokens = mutableListOf<String>()
    val readingTokens = mutableListOf<String>()

    for (kanjiStart in kanjiStartIndices) {
      val kanjiEnd = getEnd(kanji, kanjiStart, kanjiStartIndices)
      kanjiTokens.add(kanji.substring(kanjiStart, kanjiEnd))
    }

    for (kanjiToken in kanjiTokens) {
      var kanaSearch = ""

      for (i in kanjiToken.indices) {
        val codePoint = Character.codePointAt(kanji, i)
        if (CJKUtil.isKana(codePoint)) {
          kanaSearch += kanjiToken.get(i)
        }
      }

      val start = reading.indexOf(kanaSearch)
      if (start > -1 && kanaSearch.isNotEmpty()) {
        readingStartIndices.add(start + kanaSearch.length)
      }
    }

    for (readingStart in readingStartIndices) {
      val readingEnd = getEnd(reading, readingStart, readingStartIndices)
      readingTokens.add(reading.substring(readingStart, readingEnd))
    }

    for (i in kanjiTokens.indices) {
      if (builder.isNotEmpty()) {
        builder.append(" ")
      }

      val kanjiToken = kanjiTokens.get(i)
      val readingToken = readingTokens.get(i)

      addKanjiReadingPair(
          builder,
          kanjiToken,
          readingToken
      )
    }

    return builder.toString()
  }

  private fun getEnd(string: String, index: Int, list: List<Int>): Int {
    return if ((index + 1) < list.size) {
      list.get(index + 1)
    } else {
      string.length
    }
  }

  private fun addKanjiReadingPair(builder: StringBuilder, kanji: String, reading: String): StringBuilder {
    if (kanji.equals(reading)) {
      builder.append(reading)
      return builder
    }

    val commonSuffix = kanji.commonSuffixWith(reading)
    if (commonSuffix.isNotEmpty()) {

      builder.append(differenceFrom(kanji, commonSuffix))

      builder.append("[")

      builder.append(differenceFrom(reading, commonSuffix))

      builder.append("]")

      builder.append(commonSuffix)
    } else {
      builder.append("$kanji[$reading]")
    }
    return builder
  }

  private fun differenceFrom(string1: String, string2: String): String {
    // given a string like 嬉しい and another string like しい,
    // return the part that is not the same (嬉)

    val subStart = string1.indexOf(string2)
    return string1.substring(0 until subStart)
  }
}
