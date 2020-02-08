package net.emojiparty.android.jishotomo

import net.emojiparty.android.jishotomo.data.CJKUtil
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CJKUtilTest {
  @Test
  fun `isKana, when given the first Hiragana, returns true`() {
    assertThat(CJKUtil.isKana(12352), `is`(true))
  }

  @Test
  fun `isKana, when given the last Katakana, returns true`() {
    assertThat(CJKUtil.isKana(12543), `is`(true))
  }

  @Test
  fun `isKana, when given some kana in between, returns true`() {
    assertThat(CJKUtil.isKana(12500), `is`(true))

  }

  @Test
  fun `isKana, when given not kana, returns false`() {
    assertThat(CJKUtil.isKana(900), `is`(false))
  }

  @Test
  fun `isCJK, when given the first kanji, returns true`() {
    assertThat(CJKUtil.isCJK(19968), `is`(true))
  }

  @Test
  fun `isCJK, when given the ditto mark, returns true`() {
    assertThat(CJKUtil.isCJK(12293), `is`(true))
  }

  @Test
  fun `isCJK, when given some other character, returns false`() {
    assertThat(CJKUtil.isCJK(900), `is`(false))
  }
}
