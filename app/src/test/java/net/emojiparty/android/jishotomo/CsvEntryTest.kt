package net.emojiparty.android.jishotomo

import net.emojiparty.android.jishotomo.data.csv.CsvEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class CsvEntryTest {
  private val senseDisplay = SenseDisplay(TestResources(), "")

  @Test
  fun `reading, when an entry has kanji, returns the kanji followed by the reading`() {
    val entryWithKanji = makeEntry("ねこ", "猫")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("猫[ねこ]"))
  }

  @Test
  fun `reading, when an entry has kanji followed by hiragana, returns the kanji followed by the reading followed by the hiragana`() {
    val entryWithKanji = makeEntry("うれしい", "嬉しい")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("嬉[うれ]しい"))
  }

  @Test
  fun `reading, when an entry has kana followed by kanji, returns the right format`() {
    val entryWithKanji = makeEntry("ちりがみ", "ちり紙")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("ちり 紙[がみ]"))
  }

  @Test
  fun `reading, when an entry has a kanji sandwich, returns the right format`() {
    val entryWithKanji = makeEntry("くっつく", "くっ付く")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("くっ 付[つ]く"))
  }

  @Test
  fun `reading, when an entry has kanji followed by hiragana, followed by more kanji, returns the right format`() {
    val entryWithKanji = makeEntry("とこのま", "床の間")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("床[とこ]の 間[ま]"))
  }

  @Test
  fun `reading, when an entry has a kanji and kana pairs, returns the right format`() {
    val entryWithKanji = makeEntry("しみじみ", "沁み沁み")
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("沁[し]み 沁[じ]み"))
  }

  @Test
  fun `reading, when an entry does not have kanji, returns the reading`() {
    val entryWithoutKanji = makeEntry("ねこ")
    val csvEntry = CsvEntry(entryWithoutKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("ねこ"))
  }

  @Test
  fun `meaning, when only one sense, returns the parts of speech followed by the sense`() {
    val entryWithOneSense = makeEntry("ねこ", "猫", makeSense("cat", "n"))
    val csvEntry = CsvEntry(entryWithOneSense, senseDisplay)
    assertThat(csvEntry.meaning(), `is`("Noun<br/>cat<br/>"))
  }

  @Test
  fun `meaning, when more than one sense but one part of speech, returns the parts of speech followed by a numbered list of senses`() {
    val entryWithOneSense = makeEntry("ねこ", "猫", makeSense("cat", "n"), makeSense("a pretty cat"))
    val csvEntry = CsvEntry(entryWithOneSense, senseDisplay)
    assertThat(csvEntry.meaning(), `is`("Noun<br/>1. cat<br/>2. a pretty cat<br/>"))
  }

  @Test
  fun `meaning, when more than one sense but more than one part of speech, returns the parts of speech followed by a numbered list of senses`() {
    val entryWithOneSense = makeEntry(
        "ねこ", "猫", makeSense("cat", "n"), makeSense("a pretty cat"),
        makeSense("to do cat things", "v1")
    )
    val csvEntry = CsvEntry(entryWithOneSense, senseDisplay)
    assertThat(
        csvEntry.meaning(),
        `is`("Noun<br/>1. cat<br/>2. a pretty cat<br/>Verb<br/>1. to do cat things<br/>")
    )
  }

  @Test
  fun `toArray when there is a kanji returns an array with the kanji first`() {
    val entryWithKanji = makeEntry("ねこ", "猫", makeSense("cat", "n"))
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.toArray(), `is`(arrayOf("猫", "Noun<br/>cat<br/>", "猫[ねこ]")))
  }

  @Test
  fun `toArray when there is not a kanji returns an array with the reading first`() {
    val entryWithKanji = makeEntry("ねこ", null, makeSense("cat", "n"))
    val csvEntry = CsvEntry(entryWithKanji, senseDisplay)
    assertThat(csvEntry.toArray(), `is`(arrayOf("ねこ", "Noun<br/>cat<br/>", "ねこ")))
  }

  private fun makeSense(
    gloss: String,
    partsOfSpeech: String? = null
  ): SenseWithCrossReferences {
    return SenseWithCrossReferences().apply {
      this.sense = Sense().apply {
        this.glosses = gloss
        this.partsOfSpeech = partsOfSpeech
      }
    }
  }

  private fun makeEntry(
    reading: String,
    kanji: String? = null,
    vararg senses: SenseWithCrossReferences
  ): EntryWithAllSenses {
    return EntryWithAllSenses().apply {
      this.entry = Entry().apply {
        this.primaryKanji = kanji
        this.primaryReading = reading
      }
      if (senses.size > 0) {
        this.senses = ArrayList()
        this.senses.addAll(senses)
      }
    }
  }
}
