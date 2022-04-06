package net.emojiparty.android.jishotomo.ui.presentation

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.ext.splitAndJoin

object BindingMethods {
  @JvmStatic
  @BindingAdapter("visibleOrGone")
  fun setVisibility(
    view: View,
    isVisible: Boolean
  ) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
  }

  @JvmStatic
  @BindingAdapter("crossReferenceLinks")
  fun setCrossReferenceLinks(
    view: LinearLayout,
    crossReferencedEntries: List<CrossReferencedEntry>
  ) {
    if (crossReferencedEntries.isEmpty() || view.childCount > 0) {
      return
    }

    val crossReferenceButton = LegacyCrossReferenceButton(view.context)

    crossReferencedEntries.forEach { crossReferencedEntry ->
      view.addView(crossReferenceButton.create(crossReferencedEntry))
    }
  }

  @JvmStatic
  @BindingAdapter("appliesToText")
  fun setAppliesToText(
    view: TextView,
    appliesTo: String?
  ) {
    if (appliesTo == null) {
      view.text = ""
      return
    }
    val context = view.context
    val literalAppliesTo = context.getString(string.applies_to)
    val allAppliesTo = appliesTo.splitAndJoin()
    val formatted = String.format(
      context.getString(string.applies_to_format),
      literalAppliesTo,
      allAppliesTo
    )
    view.text = JapaneseLocaleSpan.mixed(formatted, literalAppliesTo)
  }

  @JvmStatic
  @BindingAdapter("jlptPill")
  fun setJlptPill(
    view: TextView,
    jlptLevel: Int?
  ) {
    if (jlptLevel != null) {
      val stringId = AndroidResourceFetcher(view.context.resources, view.context.packageName).stringForJlptLevel(jlptLevel)
      view.setText(stringId)
      view.visibility = View.VISIBLE
    } else {
      view.visibility = View.GONE
    }
  }

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
