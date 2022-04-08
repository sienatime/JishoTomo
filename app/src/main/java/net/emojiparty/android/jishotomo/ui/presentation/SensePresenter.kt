package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ext.splitAndJoin

data class SensePresenter(
  private val sense: Sense,
  val crossReferences: List<CrossReferencedEntry>
) {

  val gloss: String = sense.glosses.splitAndJoin("\n")

  val partsOfSpeechIsVisible: Boolean = sense.partsOfSpeech != null

  fun partsOfSpeech(context: Context): String {
    return SenseDisplay.formatPartsOfSpeech(sense, AndroidResourceFetcher(context.resources, context.packageName))
  }

  fun appliesToText(context: Context): String? {
    return sense.appliesTo?.let { appliesTo ->
      val literalAppliesTo = context.getString(string.applies_to)
      val allAppliesTo = appliesTo.splitAndJoin()
      return String.format(
        context.getString(string.applies_to_format),
        literalAppliesTo,
        allAppliesTo
      )
    }
  }
}
