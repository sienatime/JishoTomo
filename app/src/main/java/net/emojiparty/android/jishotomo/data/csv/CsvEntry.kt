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

    entry.senses.forEach { sense ->
      val newPartOfSpeech = appendPartsOfSpeech(builder, sense)
      if (newPartOfSpeech) {
        glossIndex = 1
      }
      if (numberOfSenses > 1) {
        builder.append(glossIndex)
        builder.append(". ")
        glossIndex++
      }
      builder.append(sense.glosses.splitAndJoin())
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
    return if (entry.hasKanji) {
      formatReading(entry.primaryKanji, entry.primaryReading)
    } else {
      entry.primaryReading
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

      kanji.indices.forEach { i ->
        val codePoint = Character.codePointAt(kanji, i)
        lastWasKana = if (CJKUtil.isKana(codePoint)) {
          true
        } else {
          if (lastWasKana) {
            kanjiStartIndices.add(i)
          }
          false
        }
      }

      buildTokensFromIndices(kanji, kanjiStartIndices)
    }
  }

  private fun buildReadingTokens(reading: String, kanji: String?, kanjiTokens: List<String>): List<String> {
    val readingStartIndices = mutableListOf(0)

    if (kanji == null && kanjiTokens.isNotEmpty()) {
      throw Exception("Kanji tokens should be empty if kanji is null")
    }

    kanjiTokens.forEach { kanjiToken ->
      var kanaSearch = ""

      kanjiToken.forEachIndexed { i, token ->
        val codePoint = Character.codePointAt(kanji!!, i)
        if (CJKUtil.isKana(codePoint)) {
          kanaSearch += token
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
        indices.forEach { start ->
          val end = getEnd(string, start, indices)
          this.add(string.substring(start, end))
        }
      }
    }
  }

  private fun getEnd(string: String, index: Int, list: List<Int>): Int {
    return if ((index + 1) < list.size) {
      list[index + 1]
    } else {
      string.length
    }
  }
}
