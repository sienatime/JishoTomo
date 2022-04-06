package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.LinearLayout.LayoutParams
import net.emojiparty.android.jishotomo.R.dimen
import net.emojiparty.android.jishotomo.R.style
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry

class LegacyCrossReferenceButton(private val context: Context) {
  private val resources: Resources = context.resources

  private val margin: Float = resources.getDimension(dimen.cross_ref_button_margin)

  fun create(crossReferencedEntry: CrossReferencedEntry): Button {
    val buttonStyleId = style.xref_button
    val button = Button(
      ContextThemeWrapper(context, buttonStyleId), null, buttonStyleId
    )
    button.text = JapaneseLocaleSpan.all(crossReferencedEntry.kanjiOrReading)
    setMargins(button)
    button.setOnClickListener {
      EntryClickHandler.open(
        context, crossReferencedEntry.id
      )
    }
    return button
  }

  private fun setMargins(button: Button) {
    val layoutParams = LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT
    )
    val marginInPixels = ScreenSize.dpsToPixels(margin, resources)
    layoutParams.marginEnd = marginInPixels
    button.layoutParams = layoutParams
  }
}
