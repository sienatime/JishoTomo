package net.emojiparty.android.jishotomo.ui.presentation

import android.text.SpannableString
import android.text.style.LocaleSpan
import java.util.Locale

object JapaneseLocaleSpan {
  fun all(finalText: CharSequence): SpannableString {
    val spannable = SpannableString(finalText)
    spannable.setSpan(LocaleSpan(Locale.JAPANESE), 0, spannable.length, 0)
    return spannable
  }

  // These TextViews have mixed locales: the first part is in the default locale,
  // and the second part is always in Japanese. This allows TalkBack to read these
  // TextViews correctly.
  fun mixed(
    finalText: CharSequence?,
    defaultLocaleText: String
  ): SpannableString {
    val spannableString = SpannableString(finalText)
    val defaultLocaleLength = defaultLocaleText.length
    spannableString.setSpan(LocaleSpan(Locale.getDefault()), 0, defaultLocaleLength, 0)
    spannableString.setSpan(
      LocaleSpan(Locale.JAPANESE), defaultLocaleLength + 1, spannableString.length, 0
    )
    return spannableString
  }
}
