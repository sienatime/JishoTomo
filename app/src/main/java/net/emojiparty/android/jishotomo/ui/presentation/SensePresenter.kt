package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.splitAndJoin

class SensePresenter(private val sense: Sense) {

  fun partsOfSpeechIsVisible(): Boolean {
    return sense.partsOfSpeech != null
  }

  fun partsOfSpeech(context: Context): String {
    return SenseDisplay.formatPartsOfSpeech(sense, AndroidResourceFetcher(context.resources), context.packageName)
  }

  fun gloss(): String {
    return sense.glosses.splitAndJoin("\n")
  }

  fun appliesToIsVisible(): Boolean {
    return sense.appliesTo != null
  }

  fun appliesTo(): String? {
    return sense.appliesTo
  }

  fun crossReferencesIsVisible(): Boolean {
    return sense.crossReferences.isNotEmpty()
  }

  fun crossReferenceLinks(): List<CrossReferencedEntry> {
    return sense.crossReferences
  }
}
