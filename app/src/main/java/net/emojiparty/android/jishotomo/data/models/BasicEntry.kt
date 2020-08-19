package net.emojiparty.android.jishotomo.data.models

abstract class BasicEntry {

  abstract var id: Int
  abstract var primaryKanji: String?
  abstract var primaryReading: String

  val hasKanji: Boolean
    get() = primaryKanji != null

  val kanjiOrReading: String
    get() = if (hasKanji) primaryKanji!! else primaryReading

  val reading: String?
    get() = if (hasKanji) primaryReading else null
}
