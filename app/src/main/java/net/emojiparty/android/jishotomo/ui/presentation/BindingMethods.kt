package net.emojiparty.android.jishotomo.ui.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter

object BindingMethods {
  // so that TalkBack will read these TextViews in Japanese
  @JvmStatic
  @BindingAdapter("textInJapanese")
  fun setTextWithJapanese(
    view: TextView,
    text: String?
  ) {
    if (text == null) {
      view.text = ""
      return
    }
    view.text = JapaneseLocaleSpan.all(text)
  }
}
