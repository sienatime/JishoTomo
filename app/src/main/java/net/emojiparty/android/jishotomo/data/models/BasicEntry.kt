package net.emojiparty.android.jishotomo.data.models

abstract class BasicEntry {

  abstract var id: Int
  abstract var primaryKanji: String?
  abstract var primaryReading: String

  fun hasKanji(): Boolean = primaryKanji != null

  fun kanjiOrReading(): String = if (hasKanji()) primaryKanji!! else primaryReading

  fun reading(): String? = if (hasKanji()) primaryReading else null
}
