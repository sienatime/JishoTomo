package net.emojiparty.android.jishotomo.data

/* CJK stands for Chinese, Japanese, Korean */

object CJKUtil {
  private val CJK_STARTING_CODEPOINT = 19968
  private val DITTO_MARK_CODEPOINT = 12293 // 々
  private val HIRAGANA_STARTING_CODEPOINT = 12352
  private val KATAKANA_END_CODEPOINT = 12543

  @JvmStatic
  fun isKana(codePoint: Int): Boolean {
    return codePoint in HIRAGANA_STARTING_CODEPOINT..KATAKANA_END_CODEPOINT
  }

  @JvmStatic
  fun isCJK(codePoint: Int): Boolean {
    return codePoint >= CJK_STARTING_CODEPOINT || codePoint == DITTO_MARK_CODEPOINT
  }

  @JvmStatic
  fun isJapanese(codePoint: Int): Boolean {
    return isKana(codePoint) || isCJK(codePoint)
  }
}
