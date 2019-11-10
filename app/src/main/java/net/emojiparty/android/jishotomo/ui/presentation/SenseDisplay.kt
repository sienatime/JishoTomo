package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import net.emojiparty.android.jishotomo.data.SemicolonSplit
import net.emojiparty.android.jishotomo.data.room.Sense
import java.util.LinkedHashSet

class SenseDisplay(private val resources: Resources, private val packageName: String) {

  constructor(context: Context): this(context.resources, context.packageName)

  fun formatPartsOfSpeech(sense: Sense): String {
    val partsOfSpeech = SemicolonSplit.split(sense.partsOfSpeech)

    val localizedPartsOfSpeech = LinkedHashSet<String>()
    for (partOfSpeech in partsOfSpeech) {
      val key = getPartOfSpeechKey(partOfSpeech)
      val stringId = resources.getIdentifier(key, "string", packageName)
      localizedPartsOfSpeech.add(resources.getString(stringId))
    }
    return TextUtils.join(", ", localizedPartsOfSpeech)
  }

  companion object {
    @JvmStatic
    fun formatPartsOfSpeech(
      sense: Sense,
      context: Context
    ): String {
      return SenseDisplay(context).formatPartsOfSpeech(sense)
    }

    private fun getPartOfSpeechKey(partOfSpeech: String): String {
      return if (partOfSpeech == "int") {
        "intj"
      } else {
        partOfSpeech.replace("-", "_")
      }
    }
  }
}
