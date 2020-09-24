package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Context
import android.content.Intent
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity
import net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity

object EntryClickHandler {
  @JvmStatic
  fun open(context: Context, entryId: Int) {
    if (context is DrawerActivity && context.isTablet()) {
      context.addDefinitionFragment(entryId)
    } else {
      val intent = Intent(context, DefinitionActivity::class.java).apply {
        this.putExtra(DefinitionFragment.ENTRY_ID_EXTRA, entryId)
      }
      context.startActivity(intent)
    }
  }
}
