package net.emojiparty.android.jishotomo

import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class SenseDisplayTest {
  val testResources = TestResources()

  @Test
  fun `formatPartsOfSpeech formats a semicolon-separated list of keys`() {
    val senseDisplay = SenseDisplay(testResources)

    assertThat(senseDisplay.formatPartsOfSpeech("n;v1"), `is`("Noun, Verb"))
  }

  @Test
  fun `formatPartsOfSpeech when called with null returns empty string`() {
    val senseDisplay = SenseDisplay(testResources)

    assertThat(senseDisplay.formatPartsOfSpeech(null), `is`(""))
  }

  @Test
  fun `getPartOfSpeechKey when given a key with a -, replaces it with a _`() {
    assertThat(SenseDisplay.getPartOfSpeechKey("my-key"), `is`("my_key"))
  }

  @Test
  fun `getPartOfSpeechKey when int, returns intj (because int is a reserved word in resources)`() {
    assertThat(SenseDisplay.getPartOfSpeechKey("int"), `is`("intj"))
  }
}
