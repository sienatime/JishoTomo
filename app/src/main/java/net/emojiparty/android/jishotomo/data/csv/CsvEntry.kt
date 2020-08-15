package net.emojiparty.android.jishotomo.data.csv

import androidx.annotation.VisibleForTesting
import net.emojiparty.android.jishotomo.data.CJKUtil
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.splitAndJoin
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay

class CsvEntry(private val entry: EntryWithAllSenses, private val senseDisplay: SenseDisplay) {
  fun toArray(): Array<String> {
    return arrayOf(entry.kanjiOrReading, meaning(), reading())
  }

  @VisibleForTesting
  fun meaning(): String {
    val builder = StringBuilder()
    val numberOfSenses = entry.senses.size
    var glossIndex = 1
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
      builder.append(sense.sense.glosses.splitAndJoin())
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
      formatReading(entry.entry.primaryKanji, entry.entry.primaryReading)
    } else {
      entry.entry.primaryReading
    }
  }

  private fun formatReading(kanji: String?, reading: String): String {
    val builder = StringBuilder()

    val kanjiTokens = buildKanjiTokens(kanji)
    val readingTokens = buildReadingTokens(reading, kanji, kanjiTokens)
    val zipped = kanjiTokens zip readingTokens

    for ((kanjiToken, readingToken) in zipped) {
      if (builder.isNotEmpty()) {
        builder.append(" ")
      }

      addKanjiReadingPair(
        builder,
        kanjiToken,
        readingToken
      )
    }

    return builder.toString()
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

  private fun buildKanjiTokens(kanji: String?): List<String> {
    return if (kanji == null) {
      emptyList()
    } else {
      val kanjiStartIndices = mutableListOf(0)
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

      buildTokensFromIndices(kanji, kanjiStartIndices)
    }
  }

  private fun buildReadingTokens(reading: String, kanji: String?, kanjiTokens: List<String>): List<String> {
    val readingStartIndices = mutableListOf(0)
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

    return buildTokensFromIndices(reading, readingStartIndices)
  }

  private fun buildTokensFromIndices(string: String?, indices: List<Int>): List<String> {
    return if (string == null) {
      emptyList()
    } else {
      mutableListOf<String>().apply {
        for (start in indices) {
          val end = getEnd(string, start, indices)
          this.add(string.substring(start, end))
        }
      }
    }
  }

  private fun getEnd(string: String, index: Int, list: List<Int>): Int {
    return if ((index + 1) < list.size) {
      list.get(index + 1)
    } else {
      string.length
    }
  }
}
