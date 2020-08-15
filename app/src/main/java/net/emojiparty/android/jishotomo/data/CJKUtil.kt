package net.emojiparty.android.jishotomo.data

/* CJK stands for Chinese, Japanese, Korean */

object CJKUtil {
  private const val CJK_STARTING_CODEPOINT = 19968
  private const val DITTO_MARK_CODEPOINT = 12293 // 々
  private const val HIRAGANA_STARTING_CODEPOINT = 12352
  private const val KATAKANA_END_CODEPOINT = 12543

  fun isKana(codePoint: Int): Boolean {
    return codePoint in HIRAGANA_STARTING_CODEPOINT..KATAKANA_END_CODEPOINT
  }

  fun isCJK(codePoint: Int): Boolean {
    return codePoint >= CJK_STARTING_CODEPOINT || codePoint == DITTO_MARK_CODEPOINT
  }

  fun isJapanese(codePoint: Int): Boolean {
    return isKana(codePoint) || isCJK(codePoint)
  }

  fun allKanji(string: String): Boolean {
    return string.indices.all {
      val codePoint = Character.codePointAt(string, it)
      isCJK(codePoint)
    }
  }
}
