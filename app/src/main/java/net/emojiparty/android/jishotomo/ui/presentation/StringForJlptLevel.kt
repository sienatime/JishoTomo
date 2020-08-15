package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context

object StringForJlptLevel {
  @JvmStatic fun getId(
    level: Int,
    context: Context
  ): Int {
    return context.resources.getIdentifier("jlpt_n$level", "string", context.packageName)
  }
}
