package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity

object EntryClickHandler {
  @JvmStatic
  fun open(context: Context, entryId: Int) {
    (context as? DrawerActivity)?.addDefinitionFragment(entryId)
  }
}
