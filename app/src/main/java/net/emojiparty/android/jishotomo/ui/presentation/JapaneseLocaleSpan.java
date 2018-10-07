package net.emojiparty.android.jishotomo.ui.presentation;

import android.text.SpannableString;
import android.text.style.LocaleSpan;
import java.util.Locale;

public class JapaneseLocaleSpan {
  public static SpannableString all(CharSequence finalText) {
    SpannableString spannable = new SpannableString(finalText);
    spannable.setSpan(new LocaleSpan(Locale.JAPANESE), 0, spannable.length(), 0);
    return spannable;
  }

  // These TextViews have mixed locales: the first part is in the default locale,
  // and the second part is always in Japanese. This allows TalkBack to read these
  // TextViews correctly.
  public static SpannableString mixed(CharSequence finalText, String defaultLocaleText) {
    SpannableString spannableString = new SpannableString(finalText);
    int defaultLocaleLength = defaultLocaleText.length();

    spannableString.setSpan(new LocaleSpan(Locale.getDefault()), 0, defaultLocaleLength, 0);
    spannableString.setSpan(new LocaleSpan(Locale.JAPANESE), defaultLocaleLength + 1, spannableString.length(), 0);
    return spannableString;
  }
}
