package net.emojiparty.android.jishotomo.ui.presentation

import androidx.annotation.VisibleForTesting
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.semicolonSplit
import java.util.LinkedHashSet

class SenseDisplay(private val resourceFetcher: ResourceFetcher) {

  fun formatPartsOfSpeech(unsplitPartsOfSpeech: String?): String {
    val partsOfSpeech = unsplitPartsOfSpeech?.semicolonSplit() ?: emptyList()
    val localizedPartsOfSpeech = LinkedHashSet<String>()
    for (partOfSpeech in partsOfSpeech) {
      val key = getPartOfSpeechKey(partOfSpeech)
      val stringId = resourceFetcher.getIdentifier(key, "string")
      localizedPartsOfSpeech.add(resourceFetcher.getString(stringId))
    }
    return localizedPartsOfSpeech.joinToString(", ")
  }

  companion object {
    fun formatPartsOfSpeech(
      sense: Sense,
      resources: ResourceFetcher
    ): String {
      return SenseDisplay(resources).formatPartsOfSpeech(sense.partsOfSpeech)
    }

    @VisibleForTesting
    fun getPartOfSpeechKey(partOfSpeech: String): String {
      return if (partOfSpeech == "int") {
        "intj"
      } else {
        partOfSpeech.replace("-", "_")
      }
    }
  }
}
