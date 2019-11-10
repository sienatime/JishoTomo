package net.emojiparty.android.jishotomo

import net.emojiparty.android.jishotomo.data.csv.CsvEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.room.Entry
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
  fun `reading, when an entry does not have kanji, returns the reading`() {
    val entryWithoutKanji = makeEntry("ねこ")
    val csvEntry = CsvEntry(entryWithoutKanji, senseDisplay)
    assertThat(csvEntry.reading(), `is`("ねこ"))
  }

  private fun makeEntry(reading: String, kanji: String? = null): EntryWithAllSenses {
    return EntryWithAllSenses().apply {
      this.entry = Entry().apply {
        this.primaryKanji = kanji
        this.primaryReading = reading
      }
    }
  }
}
